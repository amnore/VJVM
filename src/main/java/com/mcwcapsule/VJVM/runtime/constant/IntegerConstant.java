package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;

public class IntegerConstant extends ValueConstant {
    public IntegerConstant(Integer value) {
        super(value);
    }

    @Override
    public Integer getValue() {
        return (Integer) super.getValue();
    }
}
