package com.yellowbox.depfinder.test;

import com.yellowbox.depfinder.analyzer.MethodSignatureParser;
import org.junit.Assert;
import org.junit.Test;

public class TestParser
{
    @Test
    public void testParse()
    {
        String signature = "([Ljava/lang/String;)Z";

        MethodSignatureParser parser = new MethodSignatureParser("myMethod", signature);
        String parsedSignature = parser.getMethodSignature().toString();

        Assert.assertEquals("Bad signature", "boolean myMethod(java.lang.String[] arg0)", parsedSignature);
    }
}
