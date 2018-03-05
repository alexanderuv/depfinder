package com.yellowbox.depfinder.analyzer;

import java.util.ArrayList;
import java.util.List;

public class MethodSignature
{
    private String methodName;
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private String type;

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public List<ParameterDefinition> getParameters()
    {
        return parameters;
    }

    public Object getFormattedParameterList()
    {
        List<String> formParameters = new ArrayList<>();
        for (ParameterDefinition def : getParameters()) {
            formParameters.add(String.format("%s %s", def.getType(), def.getName()));
        }

        return String.join(", ", formParameters);
    }

    @Override
    public String toString()
    {
        if (getMethodName().equals("<init>")) {
            return String.format("%s(%s)",
                    getMethodName(),
                    getFormattedParameterList());
        }

        return String.format("%s %s(%s)",
                getType(),
                getMethodName(),
                getFormattedParameterList());
    }
}
