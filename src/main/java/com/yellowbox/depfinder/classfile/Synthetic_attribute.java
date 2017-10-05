package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class Synthetic_attribute extends AttributeInfo {

    Synthetic_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader reader, int name_index, int length) throws IOException {

    }
}
