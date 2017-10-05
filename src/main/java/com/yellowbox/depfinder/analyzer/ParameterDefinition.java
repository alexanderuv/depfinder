package com.yellowbox.depfinder.analyzer;

public class ParameterDefinition {
    private String type;
    private String name;

    public ParameterDefinition(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
