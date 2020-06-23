package com.mcwcapsule.VJVM.runtime.metadata;

import lombok.var;

import static com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors.DESC_reference;

public class MethodDescriptors {
    public static int getArgc(String descriptor) {
        assert descriptor.startsWith("(");
        var isParsingClass = false;
        var argc = 0;
        for (int i = 1; i < descriptor.length(); ) {
            argc += FieldDescriptors.getSize(descriptor.charAt(i));
            if (descriptor.charAt(i) == DESC_reference)
                i = descriptor.indexOf(';', i) + 1;
            else ++i;
        }
        return argc;
    }
}
