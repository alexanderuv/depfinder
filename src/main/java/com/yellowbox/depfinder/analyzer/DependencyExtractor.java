package com.yellowbox.depfinder.analyzer;

import com.yellowbox.depfinder.FinderArgs;
import com.yellowbox.depfinder.classfile.ClassFile;
import com.yellowbox.depfinder.classfile.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DependencyExtractor
{
    private static final int    BUFFER_SIZE = 8192;
    private static final Logger Log         = LoggerFactory.getLogger(DependencyExtractor.class);

    private FinderArgs   parsedArgs;
    private MatchHandler handler;

    public DependencyExtractor(final FinderArgs parsedArgs)
    {
        this.parsedArgs = parsedArgs;
    }

    public void setHandler(final MatchHandler handler)
    {
        this.handler = handler;
    }

    private boolean hasHandler()
    {
        return this.handler != null;
    }

    public void run()
    {
        if (!hasHandler()) {
            throw new RuntimeException("No handler was configured");
        }

        if (this.parsedArgs.getParameters().isEmpty()) {
            throw new RuntimeException("No arguments passed!");
        }

        if (this.parsedArgs.getTargets().isEmpty()) {
            throw new RuntimeException("No targets passed!");
        }

        for (String param : this.parsedArgs.getParameters()) {
            File f = new File(param);
            processFileSystemEntry(f, true);
        }
    }

    private void processFileSystemEntry(final File f, boolean recurse)
    {
        if (!f.isDirectory()) {
            if (Stream.of(".jar", ".class").anyMatch(e -> f.getName().toLowerCase().endsWith(e))) {
                if (!f.exists()) {
                    throw new RuntimeException("File " + f.getName() + "does not exist");
                }

                if (f.getName().toLowerCase().endsWith(".jar")) {
                    processJarFile(f.getAbsolutePath());
                } else {
                    processClassFile(f.getAbsolutePath());
                }
            } else {
                throw new RuntimeException("Only .jar and .class files are supported");
            }
        } else {
            File[] files = f.listFiles(pathname -> {
                String name = pathname.getName();
                return name.endsWith(".class") || name.endsWith(".jar");
            });
            if (files != null && recurse) {
                for (File subFile : files) {
                    processFileSystemEntry(subFile, false);
                }
            }
        }
    }

    private void processClassFile(final String name)
    {
        try (InputStream stm = Files.newInputStream(Paths.get(name))) {
            processClassFile(stm);
        } catch (Exception e) {
            Log.error("Error reading class file", e);
            //throw new RuntimeException("Error while reading file " + name, e);
        }
    }

    private void processClassFile(final InputStream stm) throws IOException
    {
        ClassFile file = new ClassFile(stm);
        if (!isClassFileExcluded(file.getClassName())) {
            List<MethodDependency> methodDependencies = getMethodDependenciesForClass(file);

            List<MethodDependency> capturedDependencies = methodDependencies.stream()
                    .filter(m -> parsedArgs.getTargets().stream().anyMatch(t -> m.getClassName().startsWith(t)))
                    .filter(m -> parsedArgs.getIgnoreList().stream().noneMatch(i -> m.getClassName().startsWith(i)))
                    .collect(Collectors.toList());

            if (!capturedDependencies.isEmpty()) {
                handler.handle(new DependencyMatch(file.getClassName(), capturedDependencies));
            }
        }
    }

    private void processJarFile(final String name)
    {
        try (InputStream stm = Files.newInputStream(Paths.get(name))) {
            System.out.println("Processing jar file: " + name);
            processJarFile(stm);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file " + name, e);
        }
    }

    private void processJarFile(final InputStream stm) throws IOException
    {
        ZipInputStream jarFile = new ZipInputStream(stm);

        while (true) {
            ZipEntry jarEntry = jarFile.getNextEntry();

            if (jarEntry == null) {
                break;
            }

            if (!jarEntry.isDirectory()) {

                if (jarEntry.getName().toLowerCase().endsWith(".jar") && !isJarExcluded(jarEntry.getName())) {
                    byte[] bytes = extractEntry(jarFile);
                    try (InputStream innerJar = new ByteArrayInputStream(bytes)) {
                        if (parsedArgs.isVerbose()) {
                            System.out.println("Processing jar file: " + jarEntry.getName());
                        }
                        processJarFile(innerJar);
                    }
                } else if (jarEntry.getName().toLowerCase().endsWith(".class")) {
                    byte[] bytes = extractEntry(jarFile);
                    try (InputStream innerJar = new ByteArrayInputStream(bytes)) {
                        if (parsedArgs.isVerbose()) {
                            System.out.println("Processing class file: " + jarEntry.getName());
                        }
                        processClassFile(innerJar);
                    } catch (Exception e) {
                        if (parsedArgs.isVerbose()) {
                            Log.error("Error reading class file " + jarEntry.getName(), e);
                            throw e;
                        } else {
                            Log.error("Error reading class file " + jarEntry.getName());
                        }
                    }
                }
            }
        }
    }

    private boolean isClassFileExcluded(final String name)
    {
        return parsedArgs.getExclusions().stream().anyMatch(name::startsWith);
    }

    private boolean isJarExcluded(final String name)
    {
        return parsedArgs.getExclusions().stream().anyMatch(name::contains);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private List<MethodDependency> getMethodDependenciesForClass(ClassFile file)
    {
        List<MethodDependency> result = new ArrayList<>();
        ConstantPool constantPool = file.getConstantPool();

        for (ConstantPool.CPInfo info : constantPool.getPool()) {
            if (info instanceof ConstantPool.CONSTANT_Methodref_info) {
                ConstantPool.CONSTANT_Methodref_info methodref = (ConstantPool.CONSTANT_Methodref_info) info;

                MethodDependency dep = getMethodDependency(methodref);

                result.add(dep);
            } else if (info instanceof ConstantPool.CONSTANT_InterfaceMethodref_info) {
                ConstantPool.CONSTANT_InterfaceMethodref_info methodref =
                        (ConstantPool.CONSTANT_InterfaceMethodref_info) info;

                MethodDependency dep = getMethodDependency(methodref);

                result.add(dep);
            }
        }

        return result;
    }

    private static byte[] extractEntry(InputStream is) throws IOException
    {
        ByteArrayOutputStream inMemoryStorage = new ByteArrayOutputStream();
        try {
            final byte[] buf = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                inMemoryStorage.write(buf, 0, length);
            }

            return inMemoryStorage.toByteArray();
        } catch (IOException e) {
            inMemoryStorage.close();
            throw new RuntimeException("Error while reading jar file", e);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_InterfaceMethodref_info methodref)
    {
        ConstantPool.CONSTANT_Class_info classInfo = methodref.getClassInfo();
        ConstantPool.CONSTANT_NameAndType_info nameTypeInfo = methodref.getNameAndType();

        return getMethodDependency(classInfo, nameTypeInfo);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_Methodref_info methodref)
    {
        ConstantPool.CONSTANT_Class_info classInfo = methodref.getClassInfo();
        ConstantPool.CONSTANT_NameAndType_info nameTypeInfo = methodref.getNameAndType();

        return getMethodDependency(classInfo, nameTypeInfo);
    }

    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_Class_info classInfo,
                                                 ConstantPool.CONSTANT_NameAndType_info nameTypeInfo)
    {
        MethodSignatureParser parser = new MethodSignatureParser(nameTypeInfo);

        return new MethodDependency(classInfo.getName().replace("/", "."), parser.getMethodSignature());
    }
}
