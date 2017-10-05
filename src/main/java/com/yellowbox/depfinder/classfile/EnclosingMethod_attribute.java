package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class EnclosingMethod_attribute extends AttributeInfo {

    private int class_index;
    private int method_index;

    EnclosingMethod_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        class_index = cr.readUnsignedShort();
        method_index = cr.readUnsignedShort();
    }
}
