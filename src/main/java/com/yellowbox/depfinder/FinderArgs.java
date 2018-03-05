package com.yellowbox.depfinder;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class FinderArgs
{
    @Parameter(description = "List of files/folders where to find the dependency")
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t", description = "Target package name or class name to look for")
    private List<String> targets = new ArrayList<>();

    @Parameter(names = "-e", description = "Exclusions (jars or class files)")
    private List<String> exclusions = new ArrayList<>();

    @Parameter(names = "-i", description = "Ignore usages")
    private List<String> ignoreList = new ArrayList<>();

    @Parameter(names = "-v", description = "Level of verbosity")
    private boolean verbose;

    @Parameter(names = "-o", description = "Output file")
    private String outputPath;

    public List<String> getIgnoreList()
    {
        return ignoreList;
    }

    public void setIgnoreList(final List<String> ignoreList)
    {
        this.ignoreList = ignoreList;
    }

    public List<String> getExclusions()
    {
        return exclusions;
    }

    public void setExclusions(final List<String> exclusions)
    {
        this.exclusions = exclusions;
    }

    public String getOutputPath()
    {
        return outputPath;
    }

    public void setOutputPath(final String outputPath)
    {
        this.outputPath = outputPath;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public void setParameters(final List<String> parameters)
    {
        this.parameters = parameters;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose(final boolean verbose)
    {
        this.verbose = verbose;
    }

    public List<String> getTargets()
    {
        return targets;
    }

    public void setTargets(final List<String> targets)
    {
        this.targets = targets;
    }
}
