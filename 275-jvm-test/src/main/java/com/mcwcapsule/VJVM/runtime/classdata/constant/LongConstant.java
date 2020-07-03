package com.mcwcapsule.VJVM.runtime.classdata.constant;

public class LongConstant extends ValueConstant {
    public LongConstant(Long value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) value;
    }
}
