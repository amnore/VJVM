package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;

public class StringConstant extends ValueConstant implements ResolvableConstant {
    private final int stringIndex;

    public StringConstant(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    @Override
    public void resolve(RuntimeConstantPool constantPool) {
        // TODO resolve
    }

    @Override
    public String getValue() {
        return (String) super.getValue();
    }
}
