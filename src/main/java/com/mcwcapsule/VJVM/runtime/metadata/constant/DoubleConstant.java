package com.mcwcapsule.VJVM.runtime.metadata.constant;

public class DoubleConstant extends ValueConstant {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public Double getValue() {
        return (Double) super.getValue();
    }
}
