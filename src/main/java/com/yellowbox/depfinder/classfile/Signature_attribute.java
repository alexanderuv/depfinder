package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class Signature_attribute extends AttributeInfo {

    private int signature_index;

    Signature_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        signature_index = cr.readUnsignedShort();
    }
}
