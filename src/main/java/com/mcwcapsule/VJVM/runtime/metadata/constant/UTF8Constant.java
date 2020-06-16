package com.mcwcapsule.VJVM.runtime.metadata.constant;

public class UTF8Constant extends ValueConstant {
    public UTF8Constant(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return (String) super.getValue();
    }
}
