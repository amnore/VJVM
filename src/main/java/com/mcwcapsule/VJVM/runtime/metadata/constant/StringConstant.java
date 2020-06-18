package com.mcwcapsule.VJVM.runtime.metadata.constant;

public class StringConstant extends ValueConstant {
    public StringConstant(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return (String) value;
    }
}
