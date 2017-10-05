package com.yellowbox.depfinder.classfile;

import java.io.IOException;

class BootstrapMethods_attribute extends AttributeInfo {

    private BootstrapMethodSpecifier[] bootstrap_method_specifiers;

    BootstrapMethods_attribute(int name_index, int length) {
        super(name_index, length);
    }

    @Override
    protected void readValues(ClassReader cr, int name_index, int length) throws IOException {
        int bootstrap_method_count = cr.readUnsignedShort();
        bootstrap_method_specifiers = new BootstrapMethodSpecifier[bootstrap_method_count];
        for (int i = 0; i < bootstrap_method_specifiers.length; i++)
            bootstrap_method_specifiers[i] = new BootstrapMethodSpecifier(cr);
    }

    public static class BootstrapMethodSpecifier {
        private int   bootstrap_method_ref;
        int[] bootstrap_arguments;

        public BootstrapMethodSpecifier(int bootstrap_method_ref, int[] bootstrap_arguments) {
            this.bootstrap_method_ref = bootstrap_method_ref;
            this.bootstrap_arguments = bootstrap_arguments;
        }

        BootstrapMethodSpecifier(ClassReader cr) throws IOException {
            bootstrap_method_ref = cr.readUnsignedShort();
            int method_count = cr.readUnsignedShort();
            bootstrap_arguments = new int[method_count];
            for (int i = 0; i < bootstrap_arguments.length; i++) {
                bootstrap_arguments[i] = cr.readUnsignedShort();
            }
        }

        int length() {
            // u2 (method_ref) + u2 (argc) + u2 * argc
            return 2 + 2 + (bootstrap_arguments.length * 2);
        }
    }
}
