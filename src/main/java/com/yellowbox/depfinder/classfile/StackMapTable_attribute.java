package com.yellowbox.depfinder.classfile;

import java.io.IOException;

public class StackMapTable_attribute extends AttributeInfo {

    private int               number_of_entries;
    private stack_map_frame[] entries;

    StackMapTable_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader reader, int name_index, int length) throws IOException {
        number_of_entries = reader.readUnsignedShort();
        entries = new stack_map_frame[number_of_entries];
        for (int i = 0; i < number_of_entries; i++) {
            entries[i] = stack_map_frame.read(reader);
        }
    }

    static abstract class stack_map_frame {

        public static stack_map_frame read(ClassReader cr) throws IOException {
            int frame_type = cr.readUnsignedByte();
            if (frame_type <= 63)
                return new same_frame(frame_type);
            else if (frame_type <= 127)
                return new same_locals_1_stack_item_frame(frame_type, cr);
            else if (frame_type <= 246)
                throw new Error("unknown frame_type " + frame_type);
            else if (frame_type == 247)
                return new same_locals_1_stack_item_frame_extended(frame_type, cr);
            else if (frame_type <= 250)
                return new chop_frame(frame_type, cr);
            else if (frame_type == 251)
                return new same_frame_extended(frame_type, cr);
            else if (frame_type <= 254)
                return new append_frame(frame_type, cr);
            else
                return new full_frame(frame_type, cr);
        }

        protected stack_map_frame(int frame_type) {
            this.frame_type = frame_type;
        }

        public int length() {
            return 1;
        }

        public abstract int getOffsetDelta();

        public final int frame_type;
    }

    public static class same_frame extends stack_map_frame {
        same_frame(int frame_type) {
            super(frame_type);
        }

        public int getOffsetDelta() {
            return frame_type;
        }
    }

    public static class same_locals_1_stack_item_frame extends stack_map_frame {
        same_locals_1_stack_item_frame(int frame_type, ClassReader cr)
                throws IOException {
            super(frame_type);
            stack = new verification_type_info[1];
            stack[0] = verification_type_info.read(cr);
        }

        @Override
        public int length() {
            return super.length() + stack[0].length();
        }

        public int getOffsetDelta() {
            return frame_type - 64;
        }

        public final verification_type_info[] stack;
    }

    public static class same_locals_1_stack_item_frame_extended extends stack_map_frame {
        same_locals_1_stack_item_frame_extended(int frame_type, ClassReader cr)
                throws IOException {
            super(frame_type);
            offset_delta = cr.readUnsignedShort();
            stack = new verification_type_info[1];
            stack[0] = verification_type_info.read(cr);
        }

        @Override
        public int length() {
            return super.length() + 2 + stack[0].length();
        }

        public int getOffsetDelta() {
            return offset_delta;
        }

        public final int                      offset_delta;
        public final verification_type_info[] stack;
    }

    public static class chop_frame extends stack_map_frame {
        chop_frame(int frame_type, ClassReader cr) throws IOException {
            super(frame_type);
            offset_delta = cr.readUnsignedShort();
        }

        @Override
        public int length() {
            return super.length() + 2;
        }

        public int getOffsetDelta() {
            return offset_delta;
        }

        public final int offset_delta;
    }

    public static class same_frame_extended extends stack_map_frame {
        same_frame_extended(int frame_type, ClassReader cr) throws IOException {
            super(frame_type);
            offset_delta = cr.readUnsignedShort();
        }

        @Override
        public int length() {
            return super.length() + 2;
        }

        public int getOffsetDelta() {
            return offset_delta;
        }

        public final int offset_delta;
    }

    public static class append_frame extends stack_map_frame {
        append_frame(int frame_type, ClassReader cr)
                throws IOException {
            super(frame_type);
            offset_delta = cr.readUnsignedShort();
            locals = new verification_type_info[frame_type - 251];
            for (int i = 0; i < locals.length; i++)
                locals[i] = verification_type_info.read(cr);
        }

        @Override
        public int length() {
            int n = super.length() + 2;
            for (verification_type_info local : locals)
                n += local.length();
            return n;
        }

        public int getOffsetDelta() {
            return offset_delta;
        }

        public final int                      offset_delta;
        public final verification_type_info[] locals;
    }

    public static class full_frame extends stack_map_frame {
        full_frame(int frame_type, ClassReader cr)
                throws IOException {
            super(frame_type);
            offset_delta = cr.readUnsignedShort();
            number_of_locals = cr.readUnsignedShort();
            locals = new verification_type_info[number_of_locals];
            for (int i = 0; i < locals.length; i++)
                locals[i] = verification_type_info.read(cr);
            number_of_stack_items = cr.readUnsignedShort();
            stack = new verification_type_info[number_of_stack_items];
            for (int i = 0; i < stack.length; i++)
                stack[i] = verification_type_info.read(cr);
        }

        @Override
        public int length() {
            int n = super.length() + 2;
            for (verification_type_info local : locals)
                n += local.length();
            n += 2;
            for (verification_type_info item : stack)
                n += item.length();
            return n;
        }

        public int getOffsetDelta() {
            return offset_delta;
        }

        public final int                      offset_delta;
        public final int                      number_of_locals;
        public final verification_type_info[] locals;
        public final int                      number_of_stack_items;
        public final verification_type_info[] stack;
    }

    public static class verification_type_info {
        public static final int ITEM_Top               = 0;
        public static final int ITEM_Integer           = 1;
        public static final int ITEM_Float             = 2;
        public static final int ITEM_Long              = 4;
        public static final int ITEM_Double            = 3;
        public static final int ITEM_Null              = 5;
        public static final int ITEM_UninitializedThis = 6;
        public static final int ITEM_Object            = 7;
        public static final int ITEM_Uninitialized     = 8;

        static verification_type_info read(ClassReader cr) throws IOException {
            int tag = cr.readUnsignedByte();
            switch (tag) {
                case ITEM_Top:
                case ITEM_Integer:
                case ITEM_Float:
                case ITEM_Long:
                case ITEM_Double:
                case ITEM_Null:
                case ITEM_UninitializedThis:
                    return new verification_type_info(tag);

                case ITEM_Object:
                    return new Object_variable_info(cr);

                case ITEM_Uninitialized:
                    return new Uninitialized_variable_info(cr);

                default:
                    throw new RuntimeException("unrecognized verification_type_info tag");
            }
        }

        protected verification_type_info(int tag) {
            this.tag = tag;
        }

        public int length() {
            return 1;
        }

        public final int tag;
    }

    public static class Object_variable_info extends verification_type_info {
        Object_variable_info(ClassReader cr) throws IOException {
            super(ITEM_Object);
            cpool_index = cr.readUnsignedShort();
        }

        @Override
        public int length() {
            return super.length() + 2;
        }

        public final int cpool_index;
    }

    public static class Uninitialized_variable_info extends verification_type_info {
        Uninitialized_variable_info(ClassReader cr) throws IOException {
            super(ITEM_Uninitialized);
            offset = cr.readUnsignedShort();
        }

        @Override
        public int length() {
            return super.length() + 2;
        }

        public final int offset;

    }
}
