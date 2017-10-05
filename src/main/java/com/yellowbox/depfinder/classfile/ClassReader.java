package com.yellowbox.depfinder.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClassReader {
    private ClassFile       classFile;
    private DataInputStream input;

    private static final AttributeFactory attributeFactory = new AttributeFactory();

    public ClassReader(ClassFile classFile, InputStream input) {
        this.classFile = classFile;
        this.input = new DataInputStream(input);
    }

    public ConstantPool getConstantPool()
    {
        return classFile.getConstantPool();
    }

    int readInt() throws IOException {
        return input.readInt();
    }

    float readFloat() throws IOException {
        return input.readFloat();
    }

    long readLong() throws IOException {
        return input.readLong();
    }

    double readDouble() throws IOException {
        return input.readDouble();
    }

    int readUnsignedByte() throws IOException {
        return input.readUnsignedByte();
    }

    int readUnsignedShort() throws IOException {
        return input.readUnsignedShort();
    }

    String readUTF() throws IOException {
        return input.readUTF();
    }

    List<Byte> readBytes(byte[] buffer) {
        try {
            Integer readBytes = input.read(buffer);
            if (!readBytes.equals(buffer.length)) {
                throw new RuntimeException("Unable to read " + buffer.length + " bytes from the stream");
            }

            List<Byte> bytes = new ArrayList<>();
            for (int i = 0; i < readBytes; i++) {
                bytes.add(buffer[i]);
            }
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException("Error reading class file", e);
        }
    }

    void checkEOF() {
        try {
            int r = input.available();
            if (r != 0) {
                throw new IOException("Expected end of file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error checking for EOF", e);
        }
    }

    AttributeInfo readAttribute() throws IOException {
        int name_index = readUnsignedShort();
        int length = readInt();
        byte[] data = new byte[length];
        readBytes(data);

        DataInputStream prev = input;
        input = new DataInputStream(new ByteArrayInputStream(data));
        try {
            return attributeFactory.createAttribute(this, name_index, data);
        } finally {
            input = prev;
        }
    }

}

