package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class InnerClasses_attribute extends AttributeInfo {

    private int    number_of_classes;
    private Info[] classes;

    InnerClasses_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        number_of_classes = cr.readUnsignedShort();
        classes = new Info[number_of_classes];
        for (int i = 0; i < number_of_classes; i++) {
            classes[i] = new Info(cr);
        }
    }

    public static class Info {
        Info(ClassReader cr) throws IOException {
            inner_class_info_index = cr.readUnsignedShort();
            outer_class_info_index = cr.readUnsignedShort();
            inner_name_index = cr.readUnsignedShort();
            inner_class_access_flags = new AccessFlags(cr.readUnsignedShort());
        }

        public Info(int inner_class_info_index, int outer_class_info_index, int inner_name_index, AccessFlags inner_class_access_flags) {
            this.inner_class_info_index = inner_class_info_index;
            this.outer_class_info_index = outer_class_info_index;
            this.inner_name_index = inner_name_index;
            this.inner_class_access_flags = inner_class_access_flags;
        }

        public ConstantPool.CONSTANT_Class_info getInnerClassInfo(ConstantPool constant_pool) {
            if (inner_class_info_index == 0)
                return null;
            return constant_pool.getClassInfo(inner_class_info_index);
        }

        public ConstantPool.CONSTANT_Class_info getOuterClassInfo(ConstantPool constant_pool) {
            if (outer_class_info_index == 0)
                return null;
            return constant_pool.getClassInfo(outer_class_info_index);
        }

        public String getInnerName(ConstantPool constant_pool) {
            if (inner_name_index == 0)
                return null;
            return constant_pool.getUTF8Value(inner_name_index);
        }

        public static int length() {
            return 8;
        }

        public final int         inner_class_info_index;
        public final int         outer_class_info_index;
        public final int         inner_name_index;
        public final AccessFlags inner_class_access_flags;
    }
}
