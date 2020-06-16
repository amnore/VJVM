package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;

public class LongConstant extends ValueConstant {
    public LongConstant(Long value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
