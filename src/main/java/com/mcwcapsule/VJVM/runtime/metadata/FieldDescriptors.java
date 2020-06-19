package com.mcwcapsule.VJVM.runtime.metadata;

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
        char f = descriptor.charAt(0);
        return (f == DESC_double || f == DESC_long) ? 2 : 1;
    }
}
