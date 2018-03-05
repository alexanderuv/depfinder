package com.yellowbox.depfinder.analyzer;

import java.util.List;

public class DependencyMatch
{
    private final String className;
    private final List<MethodDependency> capturedDependencies;

    public DependencyMatch(final String className, final List<MethodDependency> capturedDependencies)
    {
        this.className = className;
        this.capturedDependencies = capturedDependencies;
    }

    public String getClassName()
    {
        return className;
    }

    public List<MethodDependency> getCapturedDependencies()
    {
        return capturedDependencies;
    }
}
