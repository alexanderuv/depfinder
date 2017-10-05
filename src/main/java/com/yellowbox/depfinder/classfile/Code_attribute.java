package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

class Code_attribute extends AttributeInfo {

    private int               max_stack;
    private int               max_locals;
    private Integer           code_length;
    private byte[]            code;
    private int               exception_table_length;
    private ExceptionRecord[] exception_table;
    private Attributes        attributes;

    protected Code_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        max_stack = cr.readUnsignedShort();
        max_locals = cr.readUnsignedShort();
        code_length = cr.readInt();
        code = new byte[code_length];
        cr.readBytes(code);
        exception_table_length = cr.readUnsignedShort();
        exception_table = new ExceptionRecord[exception_table_length];
        for (int i = 0; i < exception_table_length; i++) {
            exception_table[i] = new ExceptionRecord(cr);
        }
        attributes = new Attributes(cr);
    }

    @Override
    void toHumanReadable(PrintStream out) {
        super.toHumanReadable(out);

        out.println("maxStack: " + max_stack);
        out.println("maxLocals: " + max_locals);
        out.println("codeLength: " + code_length);

        int counter = 0;
        for (Byte codeItem : this.code) {
            out.println("code[" + counter++ + "] -> " + codeItem);
        }

        out.println("exceptionTableLength: " + exception_table_length);
        for (int i = 0; i < exception_table_length; i++) {
            out.println("Exception [" + i + "]");
            exception_table[i].toHumanReadable(out);
            out.println();
        }

        out.println("attributesCount: " + attributes.getCount());
//        for (int i = 0; i < attributesCount; i++) {
//            attributes.get(i).toHumanReadable(out);
//        }
    }

//    @Override
//    Integer getSize() {
//        return Short.BYTES + Short.BYTES + Integer.BYTES +
//                (codeLength * Byte.BYTES) +
//                Short.BYTES +
//                (exceptionTableLength * ExceptionRecord.BYTES) +
//                Short.BYTES + // attributesCount
//                (attributesCount * 6) +
//                (attributes.stream()
//                        .map(Base_Attribute::getSize)
//                        .reduce((x, y) -> x + y).orElse(0));
//    }

    public static class ExceptionRecord {

        //static final Short BYTES = Short.BYTES * 4;

        int startPc;
        int endPc;
        int handlerPc;
        int catchType;

        ExceptionRecord(ClassReader reader) throws IOException {
            this.startPc = reader.readUnsignedShort();
            this.endPc = reader.readUnsignedShort();
            this.handlerPc = reader.readUnsignedShort();
            this.catchType = reader.readUnsignedShort();
        }

        public void toHumanReadable(PrintStream out) {
            out.println("startPc: " + startPc);
            out.println("endPc: " + endPc);
            out.println("handlerPc: " + handlerPc);
            out.println("catchType: " + catchType);
        }
    }
}
