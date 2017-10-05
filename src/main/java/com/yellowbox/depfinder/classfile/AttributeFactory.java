package com.yellowbox.depfinder.classfile;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class AttributeFactory {

    public static final String AnnotationDefault                    = "AnnotationDefault";
    public static final String BootstrapMethods                     = "BootstrapMethods";
    public static final String CharacterRangeTable                  = "CharacterRangeTable";
    public static final String Code                                 = "Code";
    public static final String ConstantValue                        = "ConstantValue";
    public static final String CompilationID                        = "CompilationID";
    public static final String Deprecated                           = "Deprecated";
    public static final String EnclosingMethod                      = "EnclosingMethod";
    public static final String Exceptions                           = "Exceptions";
    public static final String InnerClasses                         = "InnerClasses";
    public static final String LineNumberTable                      = "LineNumberTable";
    public static final String LocalVariableTable                   = "LocalVariableTable";
    public static final String LocalVariableTypeTable               = "LocalVariableTypeTable";
    public static final String MethodParameters                     = "MethodParameters";
    public static final String Module                               = "Module";
    public static final String ModuleHashes                         = "ModuleHashes";
    public static final String ModuleMainClass                      = "ModuleMainClass";
    public static final String ModulePackages                       = "ModulePackages";
    public static final String ModuleResolution                     = "ModuleResolution";
    public static final String ModuleTarget                         = "ModuleTarget";
    public static final String RuntimeVisibleAnnotations            = "RuntimeVisibleAnnotations";
    public static final String RuntimeInvisibleAnnotations          = "RuntimeInvisibleAnnotations";
    public static final String RuntimeVisibleParameterAnnotations   = "RuntimeVisibleParameterAnnotations";
    public static final String RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
    public static final String RuntimeVisibleTypeAnnotations        = "RuntimeVisibleTypeAnnotations";
    public static final String RuntimeInvisibleTypeAnnotations      = "RuntimeInvisibleTypeAnnotations";
    public static final String Signature                            = "Signature";
    public static final String SourceDebugExtension                 = "SourceDebugExtension";
    public static final String SourceFile                           = "SourceFile";
    public static final String SourceID                             = "SourceID";
    public static final String StackMap                             = "StackMap";
    public static final String StackMapTable                        = "StackMapTable";
    public static final String Synthetic                            = "Synthetic";

    private Map<String, Class<? extends AttributeInfo>> standardAttributes;

    public AttributeInfo createAttribute(ClassReader reader, int name_index, byte[] data) {
        if (standardAttributes == null) {
            init();
        }

        ConstantPool cp = reader.getConstantPool();
        String reasonForDefaultAttr;
        try {
            String name = cp.getUTF8Value(name_index);
            Class<? extends AttributeInfo> attrClass = standardAttributes.get(name);
            if (attrClass != null) {
                try {
                    Constructor<? extends AttributeInfo> noArgsCtor = attrClass.getDeclaredConstructor();
                    AttributeInfo attributeInfo = noArgsCtor.newInstance();
                    attributeInfo.initialize(reader, name_index, data.length);

                    return attributeInfo;
                } catch (Throwable t) {
                    reasonForDefaultAttr = t.toString();
                }
            } else {
                reasonForDefaultAttr = "unknown attribute";
            }
        } catch (Exception e) {
            reasonForDefaultAttr = e.toString();
        }

        return new GenericAttribute(reader, name_index, data, reasonForDefaultAttr);
    }

    protected void init() {
        standardAttributes = new HashMap<>();
        //standardAttributes.put(AnnotationDefault, AnnotationDefault_attribute.class);
        standardAttributes.put(BootstrapMethods, BootstrapMethods_attribute.class);
        //standardAttributes.put(CharacterRangeTable, CharacterRangeTable_attribute.class);
        standardAttributes.put(Code, Code_attribute.class);
        //standardAttributes.put(CompilationID, CompilationID_attribute.class);
        standardAttributes.put(ConstantValue, ConstantValue_attribute.class);
        //standardAttributes.put(Deprecated, Deprecated_attribute.class);
        standardAttributes.put(EnclosingMethod, EnclosingMethod_attribute.class);
        standardAttributes.put(Exceptions, Exceptions_attribute.class);
        standardAttributes.put(InnerClasses, InnerClasses_attribute.class);
        standardAttributes.put(LineNumberTable, LineNumberTable_attribute.class);
        standardAttributes.put(LocalVariableTable, LocalVariableTable_attribute.class);
        standardAttributes.put(LocalVariableTypeTable, LocalVariableTypeTable_attribute.class);
        standardAttributes.put(MethodParameters, MethodParameters_attribute.class);
        //standardAttributes.put(Module, Module_attribute.class);
        //standardAttributes.put(ModuleHashes, ModuleHashes_attribute.class);
        //standardAttributes.put(ModuleMainClass, ModuleMainClass_attribute.class);
        //standardAttributes.put(ModulePackages, ModulePackages_attribute.class);
        //standardAttributes.put(ModuleResolution, ModuleResolution_attribute.class);
//        //standardAttributes.put(ModuleTarget, ModuleTarget_attribute.class);
//        standardAttributes.put(RuntimeInvisibleAnnotations, RuntimeInvisibleAnnotations_attribute.class);
//        standardAttributes.put(RuntimeInvisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations_attribute.class);
//        standardAttributes.put(RuntimeVisibleAnnotations, RuntimeVisibleAnnotations_attribute.class);
//        standardAttributes.put(RuntimeVisibleParameterAnnotations, RuntimeVisibleParameterAnnotations_attribute.class);
//        standardAttributes.put(RuntimeVisibleTypeAnnotations, RuntimeVisibleTypeAnnotations_attribute.class);
//        standardAttributes.put(RuntimeInvisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations_attribute.class);
        standardAttributes.put(Signature, Signature_attribute.class);
//        standardAttributes.put(SourceDebugExtension, SourceDebugExtension_attribute.class);
        standardAttributes.put(SourceFile, SourceFile_attribute.class);
//        standardAttributes.put(SourceID, SourceID_attribute.class);
        //standardAttributes.put(StackMap, StackMap_attribute.class);
        standardAttributes.put(StackMapTable, StackMapTable_attribute.class);
        standardAttributes.put(Synthetic, Synthetic_attribute.class);
    }
}
