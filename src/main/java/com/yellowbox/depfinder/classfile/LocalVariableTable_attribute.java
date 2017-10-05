package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

class LocalVariableTable_attribute extends AttributeInfo {

    private int     local_variable_table_length;
    private Entry[] local_variable_table;

    LocalVariableTable_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        local_variable_table_length = cr.readUnsignedShort();
        local_variable_table = new Entry[local_variable_table_length];
        for (int i = 0; i < local_variable_table_length; i++) {
            local_variable_table[i] = new Entry(cr);
        }
    }

    @Override
    void toHumanReadable(PrintStream out) {
        super.toHumanReadable(out);

        out.println("lineNumberTableLength: " + local_variable_table_length);
        for (Entry e : local_variable_table) {
            e.read(out);
        }
    }

//    @Override
//    Integer getSize() {
//        return Short.BYTES + (localVariableTableLength * Entry.BYTES);
//    }

    static class Entry {

        //static final Short BYTES = Short.BYTES * 5;

        public final int start_pc;
        public final int length;
        public final int name_index;
        public final int descriptor_index;
        public final int index;

        Entry(ClassReader reader) throws IOException {
            start_pc = reader.readUnsignedShort();
            length = reader.readUnsignedShort();
            name_index = reader.readUnsignedShort();
            descriptor_index = reader.readUnsignedShort();
            index = reader.readUnsignedShort();
        }

        public void read(PrintStream out) {
            out.println("startPc: " + start_pc);
            out.println("length: " + length);
            out.println("nameIndex: " + name_index);
            out.println("descriptorIndex: " + descriptor_index);
            out.println("index: " + index);
        }
    }
}
