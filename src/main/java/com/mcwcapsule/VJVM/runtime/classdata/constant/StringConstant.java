package com.mcwcapsule.VJVM.runtime.classdata.constant;

public class StringConstant extends ValueConstant {
    public StringConstant(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return (String) value;
    }
}
