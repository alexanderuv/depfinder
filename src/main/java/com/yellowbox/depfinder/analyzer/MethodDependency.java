package com.yellowbox.depfinder.analyzer;

public class MethodDependency
{
    private String          className;
    private MethodSignature methodSignature;

    public MethodDependency(String className, MethodSignature methodSignature)
    {
        this.className = className.replace('/', '.');
        this.methodSignature = methodSignature;
    }

    public MethodSignature getMethodSignature()
    {
        return methodSignature;
    }

    public String getClassName()
    {
        return className;
    }

    @Override
    public String toString()
    {
        return String.format("[%s] %s", className, methodSignature.toString());
    }
}
