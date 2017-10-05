package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class MethodParameters_attribute extends AttributeInfo {

    private int     method_parameter_table_length;
    private Entry[] method_parameter_table;

    MethodParameters_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {

        method_parameter_table_length = cr.readUnsignedByte();
        method_parameter_table = new Entry[method_parameter_table_length];
        for (int i = 0; i < method_parameter_table_length; i++) {
            method_parameter_table[i] = new Entry(cr);
        }
    }

    public static class Entry {
        Entry(ClassReader cr) throws IOException {
            name_index = cr.readUnsignedShort();
            flags = cr.readUnsignedShort();
        }

        public static int length() {
            return 6;
        }

        public final int name_index;
        public final int flags;
    }
}
