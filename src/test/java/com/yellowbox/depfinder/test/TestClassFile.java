package com.yellowbox.depfinder.test;

import com.yellowbox.depfinder.classfile.ClassFile;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class TestClassFile
{
    @Test
    public void testClassFile() throws IOException
    {
        FileInputStream file = new FileInputStream("/Users/i078648/devlab/depfinder/src/test/resources/CollectionURIResolver.class");
        ClassFile f = new ClassFile(file);

        f.toHumanReadable(System.out);
    }
}
