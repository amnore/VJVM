package com.mcwcapsule.VJVM.runtime.metadata.constant;

public class LongConstant extends ValueConstant {
    public LongConstant(Long value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
