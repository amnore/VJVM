package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;

public class FloatConstant extends ValueConstant {
    public FloatConstant(Float value) {
        super(value);
    }

    @Override
    public Float getValue() {
        return (Float) super.getValue();
    }
}
