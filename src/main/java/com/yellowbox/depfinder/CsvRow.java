package com.yellowbox.depfinder;

public class CsvRow
{
    private String userClass;
    private String method;
    private String declaringClass;

    @SuppressWarnings("WeakerAccess")
    public CsvRow(final String userClass, final String declaringClass, final String method)
    {
        this.userClass = userClass;
        this.declaringClass = declaringClass;
        this.method = method;
    }

    public String getUserClass()
    {
        return userClass;
    }

    public void setUserClass(final String userClass)
    {
        this.userClass = userClass;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(final String method)
    {
        this.method = method;
    }

    public String getDeclaringClass()
    {
        return declaringClass;
    }

    public void setDeclaringClass(final String declaringClass)
    {
        this.declaringClass = declaringClass;
    }
}
