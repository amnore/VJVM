package com.mcwcapsule.VJVM.runtime.metadata.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class ValueConstant extends Constant {
    @Setter(value = AccessLevel.PROTECTED)
    @Getter
    protected Object value;

    public ValueConstant() {
    }

    public ValueConstant(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
