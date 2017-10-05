package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class GenericAttribute extends AttributeInfo {
    GenericAttribute(ClassReader cr, int name_index, byte[] data) {
        this(cr, name_index, data, null);
    }

    GenericAttribute(ClassReader cr, int name_index, byte[] data, String reason) {
        super(name_index, data.length);
        info = data;
        this.reason = reason;
    }

    public GenericAttribute(ConstantPool constant_pool, int name_index, byte[] info) {
        this(constant_pool, name_index, info, null);
    }

    public GenericAttribute(ConstantPool constant_pool, int name_index,
                            byte[] info, String reason) {
        super(name_index, info.length);
        this.info = info;
        this.reason = reason;
    }

    public final byte[] info;

    public final String reason; // reason to use generic

    @Override
    protected void readValues(ClassReader reader, int name_index, int length) throws IOException {
    }
}
