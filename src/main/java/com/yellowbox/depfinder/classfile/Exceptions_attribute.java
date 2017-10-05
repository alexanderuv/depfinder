package com.yellowbox.depfinder.classfile;

import java.io.IOException;

class Exceptions_attribute extends AttributeInfo {

    private int   number_of_exceptions;
    private int[] exception_index_table;

    Exceptions_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        number_of_exceptions = cr.readUnsignedShort();
        exception_index_table = new int[number_of_exceptions];
        for (int i = 0; i < number_of_exceptions; i++) {
            exception_index_table[i] = cr.readUnsignedShort();
        }
    }
}
