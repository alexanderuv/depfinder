package com.yellowbox.depfinder;

import com.yellowbox.depfinder.analyzer.DependencyExtractor;
import com.yellowbox.depfinder.analyzer.MethodDependency;
import com.yellowbox.depfinder.classfile.ClassFile;

import java.io.IOException;
import java.util.List;

public class Program {
    public static void main(String[] args) throws IOException {
        ClassFile file = ClassFile.read("/Users/alexander/devlab/depfinder/target/test-classes/com/yellowbox/depfinder/test/SampleClass.class");

        DependencyExtractor extractor = new DependencyExtractor();

        List<MethodDependency> dependencies = extractor.getMethodDependenciesForClass(file);
        for(MethodDependency dep : dependencies) {
            System.out.println(dep.toString());
        }
    }
}
