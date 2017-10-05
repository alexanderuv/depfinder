package com.yellowbox.depfinder.classfile;

import java.io.IOException;
import java.io.PrintStream;

abstract class BaseMemberInfo {

    AccessFlags access_flags;
    int         name_index;
    int         descriptor_index;
    Attributes  attributes;

    BaseMemberInfo(ClassReader reader) throws IOException {
        access_flags = new AccessFlags(reader);
        name_index  = reader.readUnsignedShort();
        descriptor_index = reader.readUnsignedShort();
        attributes = new Attributes(reader);
    }

    void toHumanReadable(PrintStream out) {
        out.println("Access Flags: " + access_flags.toString());
        out.println("Name Index: " + name_index);
        out.println("Descriptor Index: " + descriptor_index);
        out.println("Attribute count: " + attributes.getCount());

//        for (Base_Attribute att : attributes) {
//            att.toHumanReadable(out);
//        }
    }
}
