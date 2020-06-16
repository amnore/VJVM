package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;

public class DoubleConstant extends ValueConstant {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public Double getValue() {
        return (Double) super.getValue();
    }
}
