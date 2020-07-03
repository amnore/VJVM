package com.mcwcapsule.VJVM.classfiledefs;

public class FieldDescriptors {
    public static final char DESC_byte = 'B';
    public static final char DESC_char = 'C';
    public static final char DESC_double = 'D';
    public static final char DESC_float = 'F';
    public static final char DESC_int = 'I';
    public static final char DESC_long = 'J';
    public static final char DESC_reference = 'L';
    public static final char DESC_short = 'S';
    public static final char DESC_boolean = 'Z';
    public static final char DESC_array = '[';

    /**
     * Get the size (in slots) of a type.
     */
    public static int getSize(String descriptor) {
        return getSize(descriptor.charAt(0));
    }

    public static int getSize(char c) {
        return (c == DESC_double || c == DESC_long) ? 2 : 1;
    }

    public static boolean isReference(String descriptor) {
        return isReference(descriptor.charAt(0));
    }

    public static boolean isReference(char c) {
        return c == DESC_reference || c == DESC_array;
    }
}
