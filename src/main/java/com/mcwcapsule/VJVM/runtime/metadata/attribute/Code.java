package com.mcwcapsule.VJVM.runtime.metadata.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Code extends Attribute {
    @Getter
    private final int maxStack;
    @Getter
    private final int maxLocals;
    @Getter
    private final byte[] code;
    @Getter
    private final Attribute[] attributes;
}
