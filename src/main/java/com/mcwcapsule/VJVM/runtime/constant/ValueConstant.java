package com.mcwcapsule.VJVM.runtime.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class ValueConstant {
    @Setter(value = AccessLevel.PROTECTED)
    @Getter
    private Object value;

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
