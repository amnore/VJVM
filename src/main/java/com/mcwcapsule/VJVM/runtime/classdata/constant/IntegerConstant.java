package com.mcwcapsule.VJVM.runtime.classdata.constant;

public class IntegerConstant extends ValueConstant {
    public IntegerConstant(Integer value) {
        super(value);
    }

    @Override
    public Integer getValue() {
        return (Integer) value;
    }
}
