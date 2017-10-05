package com.yellowbox.depfinder.analyzer;

import com.yellowbox.depfinder.classfile.ConstantPool;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodSignatureParser {

    private MethodSignature methodSignature;
    private int argCounter = 0;

    public MethodSignatureParser(String name, String signature) {
        methodSignature = new MethodSignature();
        methodSignature.setMethodName(name);
        parse(signature);
    }

    public MethodSignatureParser(ConstantPool.CONSTANT_NameAndType_info nameAndType) {
        methodSignature = new MethodSignature();
        methodSignature.setMethodName(nameAndType.getName());
        parse(nameAndType.getSignature());
    }

    private void parse(String signature) {

        try (StringReader reader = new StringReader(signature)) {
            if (reader.read() != '(') {
                throw new RuntimeException("Expected '(' at pos 0");
            }

            List<ParameterDefinition> parameters = readParameters(reader);

            int type = reader.read();
            String actualType = parseType(reader, type);

            methodSignature.setType(actualType);
            methodSignature.getParameters().addAll(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing method signature: " + signature, e);
        }
    }

    private List<ParameterDefinition> readParameters(StringReader reader) throws IOException {
        List<ParameterDefinition> parameters = new ArrayList<>();

        int type = reader.read();
        while(type != ')') {
            String actualType = parseType(reader, type);
            type = reader.read();
            parameters.add(new ParameterDefinition(actualType, "arg" + (argCounter++)));
        }

        return parameters;
    }

    private String parseType(StringReader reader, int type) throws IOException {
        String actualType = mapTypeToReadableType(type);

        if (actualType.equals("#")) {
            StringBuilder actualTypeBuilder = new StringBuilder();
            int last;
            do {
                last = reader.read();
                actualTypeBuilder.append((char) last);
            }
            while (last != ';');

            actualType = actualTypeBuilder.toString();
            actualType = actualType.substring(0, actualType.length() - 1);
        }

        return actualType.replace("/", ".");
    }

    private String mapTypeToReadableType(int type) {
        switch (type) {
            case 'V':
                return "void";
            case 'L':
                return "#";
        }

        return "UNDEFINED";
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }
}
