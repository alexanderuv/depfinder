package com.yellowbox.depfinder.analyzer;

import com.yellowbox.depfinder.classfile.ClassFile;
import com.yellowbox.depfinder.classfile.ConstantPool;

import java.util.ArrayList;
import java.util.List;

public class DependencyExtractor {

    public List<MethodDependency> getMethodDependenciesForClass(ClassFile file) {
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

    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_InterfaceMethodref_info methodref) {
        ConstantPool.CONSTANT_Class_info classInfo = methodref.getClassInfo();
        ConstantPool.CONSTANT_NameAndType_info nameTypeInfo = methodref.getNameAndType();

        return getMethodDependency(classInfo, nameTypeInfo);
    }

    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_Methodref_info methodref) {
        ConstantPool.CONSTANT_Class_info classInfo = methodref.getClassInfo();
        ConstantPool.CONSTANT_NameAndType_info nameTypeInfo = methodref.getNameAndType();

        return getMethodDependency(classInfo, nameTypeInfo);
    }

    private MethodDependency getMethodDependency(ConstantPool.CONSTANT_Class_info classInfo, ConstantPool.CONSTANT_NameAndType_info nameTypeInfo) {
        MethodSignatureParser parser = new MethodSignatureParser(nameTypeInfo);

        return new MethodDependency(classInfo.getName(), parser.getMethodSignature());
    }
}
