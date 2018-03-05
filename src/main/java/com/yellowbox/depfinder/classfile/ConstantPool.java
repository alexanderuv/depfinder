package com.yellowbox.depfinder.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConstantPool implements Iterable<ConstantPool.CPInfo>
{

    private static final byte CONSTANT_Class              = 7;
    private static final byte CONSTANT_Fieldref           = 9;
    private static final byte CONSTANT_Methodref          = 10;
    private static final byte CONSTANT_InterfaceMethodref = 11;
    private static final byte CONSTANT_String             = 8;
    private static final byte CONSTANT_Integer            = 3;
    private static final byte CONSTANT_Float              = 4;
    private static final byte CONSTANT_Long               = 5;
    private static final byte CONSTANT_Double             = 6;
    private static final byte CONSTANT_NameAndType        = 12;
    private static final byte CONSTANT_Utf8               = 1;
    private static final byte CONSTANT_MethodHandle       = 15;
    private static final byte CONSTANT_MethodType         = 16;
    private static final byte CONSTANT_InvokeDynamic      = 18;

    private int          count;
    private CPInfo[]     pool;
    private List<CPInfo> writePool;

    public ConstantPool()
    {
        writePool = new ArrayList<>();
    }

    ConstantPool(ClassReader reader) throws IOException
    {

        count = reader.readUnsignedShort();
        pool = new CPInfo[count];
        for (int i = 1; i < count; i++) {
            int tag = reader.readUnsignedByte();

            CPInfo info;
            switch (tag) {
                case CONSTANT_Class:
                    info = new CONSTANT_Class_info(this, reader);
                    break;
                case CONSTANT_Fieldref:
                    info = new CONSTANT_Fieldref_info(this, reader, tag);
                    break;
                case CONSTANT_Methodref:
                    info = new CONSTANT_Methodref_info(this, reader, tag);
                    break;
                case CONSTANT_InterfaceMethodref:
                    info = new CONSTANT_InterfaceMethodref_info(this, reader, tag);
                    break;
                case CONSTANT_String:
                    info = new CONSTANT_String_info(this, reader);
                    break;
                case CONSTANT_Integer:
                    info = new CONSTANT_Integer_info(this, reader);
                    break;
                case CONSTANT_Float:
                    info = new CONSTANT_Float_info(this, reader);
                    break;
                case CONSTANT_Long:
                    info = new CONSTANT_Long_info(this, reader);
                    i++;
                    break;
                case CONSTANT_Double:
                    info = new CONSTANT_Double_info(this, reader);
                    i++;
                    break;
                case CONSTANT_NameAndType:
                    info = new CONSTANT_NameAndType_info(this, reader);
                    break;
                case CONSTANT_Utf8:
                    info = new CONSTANT_Utf8_info(this, reader);
                    break;
                case CONSTANT_MethodHandle:
                    info = new CONSTANT_MethodHandle_info(this, reader);
                    break;
                case CONSTANT_MethodType:
                    info = new CONSTANT_MethodType_info(this, reader);
                    break;
                case CONSTANT_InvokeDynamic:
                    info = new CONSTANT_InvokeDynamic_info(this, reader);
                    break;

                default:
                    throw new RuntimeException("Unrecognized constant type: " + tag);
            }

            pool[i] = info;
        }
    }

    public int getCount()
    {
        return writePool != null ? writePool.size() : count;
    }

    String getUTF8Value(int index)
    {
        return getUTF8(index).getValue();
    }

    CONSTANT_Utf8_info getUTF8(int index)
    {
        return (CONSTANT_Utf8_info) get(index, CONSTANT_Utf8);
    }

    CONSTANT_Class_info getClassInfo(int index)
    {
        return (CONSTANT_Class_info) get(index, CONSTANT_Class);
    }

    private CPInfo get(int index)
    {
        if (index <= 0 || index >= pool.length) {
            throw new RuntimeException("Index does not exist in constant pool");
        }
        CPInfo info = pool[index];
        if (info == null) {
            throw new RuntimeException("Index does not exist in constant pool");
        }
        return pool[index];
    }

    private CPInfo get(int index, int expectedTag)
    {
        CPInfo info = get(index);
        if (info.getTag() != expectedTag) {
            throw new IllegalArgumentException("Element at index " + index + " is not of expected type " +
                                               expectedTag + " but is " + info.getTag());
        }
        return info;
    }

    public CPInfo[] getPool()
    {
        return pool;
    }

    @Override
    public Iterator<CPInfo> iterator()
    {
        return Arrays.asList(pool).iterator();
    }

    public void addConstant(CPInfo constant)
    {
        this.writePool.add(constant);
    }

    public int indexOf(CPInfo constant)
    {
        return writePool.indexOf(constant) + 1;
    }

    public static abstract class CPInfo
    {

        CPInfo()
        {
            this.cp = null;
        }

        CPInfo(ConstantPool cp)
        {
            this.cp = cp;
        }

        ConstantPool cp;

        abstract byte getTag();

        abstract int getByteLength();

        public void toHumanReadable(PrintStream out)
        {
            out.println("Not implemented");
        }
    }

    public static abstract class CPRefInfo extends CPInfo
    {

        private final int tag;
        int class_index;
        int name_and_type_index;

        CPRefInfo(ConstantPool cp, ClassReader reader, int tag) throws IOException
        {
            super(cp);
            this.tag = tag;
            class_index = reader.readUnsignedShort();
            name_and_type_index = reader.readUnsignedShort();
        }

        @Override
        public byte getTag()
        {
            return (byte) tag;
        }

        @Override
        int getByteLength()
        {
            return 5;
        }

        public CONSTANT_Class_info getClassInfo()
        {
            return (CONSTANT_Class_info) cp.getPool()[class_index];
        }

        public CONSTANT_NameAndType_info getNameAndType()
        {
            return (CONSTANT_NameAndType_info) cp.getPool()[name_and_type_index];
        }

        @Override
        public String toString()
        {
            return String.format("#%d.#%d", class_index, name_and_type_index);
        }
    }

    public static class CONSTANT_Class_info extends CPInfo
    {

        private int name_index;

        CONSTANT_Class_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            this.name_index = reader.readUnsignedShort();
        }

        public CONSTANT_Class_info(ConstantPool cp, int name_index)
        {
            super(cp);
            this.name_index = name_index;
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Class;
        }

        int getByteLength()
        {
            return 3; // 1 (tag) + 2 (Short.BYTES)
        }

        public short getNameIndex()
        {
            return (short) name_index;
        }

        public String getName()
        {
            return cp.getUTF8Value(name_index);
        }

        @Override
        public String toString()
        {
            return "#" + name_index;
        }
    }

    public class CONSTANT_Fieldref_info extends CPRefInfo
    {

        CONSTANT_Fieldref_info(ConstantPool cp, ClassReader reader, int tag) throws IOException
        {
            super(cp, reader, tag);
        }
    }

    public class CONSTANT_Methodref_info extends CPRefInfo
    {

        CONSTANT_Methodref_info(ConstantPool cp, ClassReader reader, int tag) throws IOException
        {
            super(cp, reader, tag);
        }
    }

    public class CONSTANT_InterfaceMethodref_info extends CPRefInfo
    {

        CONSTANT_InterfaceMethodref_info(ConstantPool cp, ClassReader reader, int tag) throws IOException
        {
            super(cp, reader, tag);
        }
    }

    public static class CONSTANT_String_info extends CPInfo
    {

        private final int string_index;

        CONSTANT_String_info(ConstantPool cp, ClassReader cr) throws IOException
        {
            super(cp);
            string_index = cr.readUnsignedShort();
        }

        public CONSTANT_String_info(ConstantPool cp, int string_index)
        {
            super(cp);
            this.string_index = string_index;
        }

        @Override
        byte getTag()
        {
            return CONSTANT_String;
        }

        @Override
        int getByteLength()
        {
            return 3;
        }

        public String getString()
        {
            return cp.getUTF8Value(string_index);
        }

        @Override
        public String toString()
        {
            return "#" + string_index;
        }
    }

    public class CONSTANT_Integer_info extends CPInfo
    {

        private final int value;

        CONSTANT_Integer_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            value = reader.readInt();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Integer;
        }

        @Override
        int getByteLength()
        {
            return 5;
        }

        int getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "" + value;
        }
    }

    public class CONSTANT_Float_info extends CPInfo
    {

        private final float value;

        CONSTANT_Float_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            value = reader.readFloat();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Float;
        }

        @Override
        int getByteLength()
        {
            return 5;
        }

        float getValue()
        {
            return value;
        }
    }

    public class CONSTANT_Long_info extends CPInfo
    {

        private final long value;

        CONSTANT_Long_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            value = reader.readLong();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Long;
        }

        @Override
        int getByteLength()
        {
            return 9;
        }

        long getValue()
        {
            return value;
        }
    }

    public class CONSTANT_Double_info extends CPInfo
    {

        private final double value;

        CONSTANT_Double_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            value = reader.readDouble();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Double;
        }

        @Override
        int getByteLength()
        {
            return 9;
        }

        double getValue()
        {
            return value;
        }
    }

    public class CONSTANT_NameAndType_info extends CPInfo
    {

        private final int name_index;
        private final int descriptor_index;

        CONSTANT_NameAndType_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            name_index = reader.readUnsignedShort();
            descriptor_index = reader.readUnsignedShort();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_NameAndType;
        }

        @Override
        int getByteLength()
        {
            return 5;
        }

        public String getName()
        {
            return cp.getUTF8Value(name_index);
        }

        public String getSignature()
        {
            return cp.getUTF8Value(descriptor_index);
        }

        @Override
        public String toString()
        {
            return String.format("#%d.#%d // %s:%s", name_index, descriptor_index,
                    getName(), getSignature());
        }
    }

    public static class CONSTANT_Utf8_info extends CPInfo
    {

        private final String value;

        CONSTANT_Utf8_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            value = reader.readUTF();
        }

        public CONSTANT_Utf8_info(String value)
        {
            this.value = value;
        }

        @Override
        byte getTag()
        {
            return CONSTANT_Utf8;
        }

        @Override
        int getByteLength()
        {
            class SizeOutputStream extends OutputStream
            {
                @Override
                public void write(int b)
                {
                    size++;
                }

                private int size;
            }
            SizeOutputStream sizeOut = new SizeOutputStream();
            DataOutputStream out = new DataOutputStream(sizeOut);
            try {
                out.writeUTF(value);
            } catch (IOException ignore) {
            }
            return 1 + sizeOut.size;
        }

        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return getValue();
        }
    }

    public class CONSTANT_MethodHandle_info extends CPInfo
    {

        private final int reference_kind;
        private final int reference_index;

        CONSTANT_MethodHandle_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            reference_kind = reader.readUnsignedByte();
            reference_index = reader.readUnsignedShort();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_MethodHandle;
        }

        @Override
        int getByteLength()
        {
            return 4;
        }
    }

    public class CONSTANT_MethodType_info extends CPInfo
    {
        private final int descriptor_index;

        CONSTANT_MethodType_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            descriptor_index = reader.readUnsignedShort();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_MethodType;
        }

        @Override
        int getByteLength()
        {
            return 3;
        }
    }

    public class CONSTANT_InvokeDynamic_info extends CPInfo
    {

        private final int bootstrap_method_attr_index;
        private final int name_and_type_index;

        CONSTANT_InvokeDynamic_info(ConstantPool cp, ClassReader reader) throws IOException
        {
            super(cp);
            bootstrap_method_attr_index = reader.readUnsignedShort();
            name_and_type_index = reader.readUnsignedShort();
        }

        @Override
        byte getTag()
        {
            return CONSTANT_InvokeDynamic;
        }

        @Override
        int getByteLength()
        {
            return 5;
        }
    }
}
