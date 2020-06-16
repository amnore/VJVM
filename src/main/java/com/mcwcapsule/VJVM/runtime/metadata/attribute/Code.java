package com.mcwcapsule.VJVM.runtime.metadata.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Code extends Attribute {
    @Getter
    private int maxStack;
    @Getter
    private int maxLocals;
    @Getter
    private byte[] code;
    @Getter
    private Attribute[] attributes;
}
