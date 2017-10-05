package com.yellowbox.depfinder.classfile;

import java.io.IOException;

class ConstantValue_attribute extends AttributeInfo {

    private int constantvalue_index;

    ConstantValue_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader reader, int name_index, int length) throws IOException {
        constantvalue_index = reader.readUnsignedShort();
    }
}
