package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

class SourceFile_attribute extends AttributeInfo {

    private int sourcefile_index;

    SourceFile_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader reader, int name_index, int length) throws IOException {
        sourcefile_index = reader.readUnsignedShort();
    }

    @Override
    void toHumanReadable(PrintStream out) {
        super.toHumanReadable(out);
        out.println("sourceFileIndex: " + sourcefile_index);
    }

//    @Override
//    Integer getSize() {
//        return Short.BYTES;
//    }
}
