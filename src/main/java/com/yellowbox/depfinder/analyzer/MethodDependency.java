package com.yellowbox.depfinder.analyzer;

public class MethodDependency {

    private String className;
    private MethodSignature methodSignature;

    public MethodDependency(String className, MethodSignature methodSignature) {
        this.className = className;
        this.methodSignature = methodSignature;
    }

    public String getClassName() {
        return className;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    @Override
    public String toString() {
        if(methodSignature.getMethodName().equals("<init>")) {
            return String.format("[%s] %s(%s)", className.replace('/', '.'),
                    methodSignature.getMethodName(),
                    methodSignature.getFormattedParameterList());
        }

        return String.format("[%s] %s %s(%s)", className.replace('/', '.'),
                methodSignature.getType(),
                methodSignature.getMethodName(),
                methodSignature.getFormattedParameterList());
    }
}
