package com.yellowbox.depfinder.classfile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class ClassFile {

    static int JAVA_MAGIC_NUMBER = 0xCAFEBABE;

    private int          magic;
    private int          minor_version;
    private int          major_version;
    private ConstantPool constant_pool;
    private AccessFlags  access_flags;
    private int          this_class;
    private int          super_class;
    private int          interfaces_count;
    private int[]        interfaces;
    private int          fields_count;
    private FieldInfo[]  fields;
    private int          method_count;
    private MethodInfo[] methods;
    private Attributes   attributes;

    public ClassFile() {
        magic = JAVA_MAGIC_NUMBER;
        minor_version = 0;
        major_version = 52;
        constant_pool = new ConstantPool();
        access_flags = new AccessFlags();
    }

    public ClassFile(InputStream input) throws IOException {
        ClassReader reader = new ClassReader(this, input);

        magic = reader.readInt();
        if (magic != ClassFile.JAVA_MAGIC_NUMBER)
            throw new RuntimeException("Unable to find magic number in class file");

        minor_version = reader.readUnsignedShort();
        major_version = reader.readUnsignedShort();

        constant_pool = new ConstantPool(reader);
        access_flags = new AccessFlags(reader);
        this_class = reader.readUnsignedShort();
        super_class = reader.readUnsignedShort();

        interfaces_count = reader.readUnsignedShort();
        interfaces = new int[interfaces_count];
        for (int i = 0; i < interfaces_count; i++) {
            interfaces[i] = reader.readUnsignedShort();
        }

        fields_count = reader.readUnsignedShort();
        fields = new FieldInfo[fields_count];
        for (int i = 0; i < fields_count; i++) {
            fields[i] = new FieldInfo(reader);
        }

        method_count = reader.readUnsignedShort();
        methods = new MethodInfo[method_count];
        for (int i = 0; i < method_count; i++) {
            methods[i] = new MethodInfo(reader);
        }

        attributes = new Attributes(reader);

        reader.checkEOF();
    }

    public void toHumanReadable(PrintStream out) {
        out.println("Magic Number OK: 0x" + Integer.toHexString(JAVA_MAGIC_NUMBER).toUpperCase());

        out.println("Minor Version: " + minor_version);
        out.println("Major Version: " + major_version);

        out.println("Constant Pool Count: " + constant_pool.getCount());
        int count = 1;
        for (ConstantPool.CPInfo constant : constant_pool.getPool()) {
            out.print("[" + count + "]");
            count++;

            constant.toHumanReadable(out);
        }

        out.println("Access Flags: " + access_flags.toString());
        out.println("This Class: " + this_class);
        out.println("Super Class: " + super_class);

        out.println("Interface count: " + interfaces_count);
        for (int i : interfaces) {
            out.println("Interface: " + i);
        }

        out.println();
        out.println("Fields Count: " + fields_count);
        for (FieldInfo fieldInfo : fields) {
            out.println("Field:");
            fieldInfo.toHumanReadable(out);
            out.println();
        }

        out.println();
        out.println("Methods Count: " + method_count);
        for (MethodInfo method : methods) {
            out.println("Method:");
            method.toHumanReadable(out);
            out.println();
        }

        attributes.toHumanReadable(out);
    }

    public static ClassFile read(String path) throws IOException {
        try (FileInputStream fin = new FileInputStream(path)) {
            return new ClassFile(fin);
        }
    }

    public static ClassFile read(InputStream stream) throws IOException {
        return new ClassFile(stream);
    }

    public ConstantPool getConstantPool() {
        return constant_pool;
    }
}

