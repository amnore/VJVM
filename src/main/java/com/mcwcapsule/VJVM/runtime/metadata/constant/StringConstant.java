package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

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
