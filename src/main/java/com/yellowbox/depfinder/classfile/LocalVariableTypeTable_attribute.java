package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class LocalVariableTypeTable_attribute extends AttributeInfo {

    private int     local_variable_table_length;
    private Entry[] local_variable_table;

    LocalVariableTypeTable_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        local_variable_table_length = cr.readUnsignedShort();
        local_variable_table = new Entry[local_variable_table_length];
        for (int i = 0; i < local_variable_table_length; i++)
            local_variable_table[i] = new Entry(cr);
    }

    public static class Entry {
        Entry(ClassReader cr) throws IOException {
            start_pc = cr.readUnsignedShort();
            length = cr.readUnsignedShort();
            name_index = cr.readUnsignedShort();
            signature_index = cr.readUnsignedShort();
            index = cr.readUnsignedShort();
        }

        public static int length() {
            return 10;
        }

        public final int start_pc;
        public final int length;
        public final int name_index;
        public final int signature_index;
        public final int index;
    }
}
