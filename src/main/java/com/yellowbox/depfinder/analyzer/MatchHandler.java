package com.yellowbox.depfinder.analyzer;

@FunctionalInterface
public interface MatchHandler
{
    void handle(DependencyMatch match);
}
