package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

class LineNumberTable_attribute extends AttributeInfo {

    private int     line_number_table_length;
    private Entry[] line_number_table;

    LineNumberTable_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        line_number_table_length = cr.readUnsignedShort();
        line_number_table = new Entry[line_number_table_length];
        for (int i = 0; i < line_number_table_length; i++) {
            line_number_table[i] = new Entry(cr);
        }
    }

    @Override
    void toHumanReadable(PrintStream out) {
        super.toHumanReadable(out);

        out.println("line_number_table_length: " + line_number_table_length);

        int count = 0;
        for (Entry e : line_number_table) {
            out.println("LineNumberTable Entry [" + count++ + "]");
            e.read(out);
        }
    }

//    @Override
//    Integer getSize() {
//        return Short.BYTES + (lineNumberTableLength * Entry.BYTES);
//    }

    static class Entry {

        static final Short BYTES = Short.BYTES + Short.BYTES;

        int startPc;
        int lineNumber;

        Entry(ClassReader reader) throws IOException {
            startPc = reader.readUnsignedShort();
            lineNumber = reader.readUnsignedShort();
        }

        void read(PrintStream out) {
            out.println("startPc: " + startPc);
            out.println("lineNumber: " + lineNumber);
        }
    }
}
