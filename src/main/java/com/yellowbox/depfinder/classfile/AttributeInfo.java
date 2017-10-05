package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

abstract class AttributeInfo {

    int attribute_name_index;
    int attribute_length;

    AttributeInfo(int name_index, int length) {
        initialize(name_index, length);
    }

    void initialize(int name_index, int length) {
        attribute_name_index = name_index;
        attribute_length = length;
    }

    public static AttributeInfo read(ClassReader reader) throws IOException {
        return reader.readAttribute();
    }

    public int getByteLength() {
        return 6 + attribute_length;
    }

    void toHumanReadable(PrintStream out) {
        out.println("Attribute ==");
        out.println("Name: " + this.getClass().getSimpleName().replace("_Attribute", ""));
        out.println("Size: " + getByteLength() + ", Read=" + attribute_length);
    }

    String getName(ConstantPool cp) {
        return cp.getUTF8Value(attribute_name_index);
    }

    final void initialize(ClassReader reader, int name_index, int length) throws IOException {
        initialize(name_index, length);

        readValues(reader, name_index, length);
    }

    protected abstract void readValues(ClassReader reader, int name_index, int length) throws IOException;
}
