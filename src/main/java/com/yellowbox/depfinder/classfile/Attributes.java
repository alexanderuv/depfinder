package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Attributes implements Iterable<AttributeInfo> {

    private final AttributeInfo[] attrs;
    private final Map<String, AttributeInfo> map = new HashMap<>();
    private int count;

    Attributes(ClassReader reader) throws IOException {
        count = reader.readUnsignedShort();
        attrs = new AttributeInfo[count];

        for (int i = 0; i < count; i++) {
            AttributeInfo attr = AttributeInfo.read(reader);
            attrs[i] = attr;
            try {
                map.put(attr.getName(reader.getConstantPool()), attr);
            } catch (Exception e) {
                // don't enter invalid names in map
            }
        }
    }

    public void toHumanReadable(PrintStream out) {

    }

    @Override
    public Iterator<AttributeInfo> iterator() {
        return Arrays.asList(attrs).iterator();
    }

    public int getCount() {
        return count;
    }
}
