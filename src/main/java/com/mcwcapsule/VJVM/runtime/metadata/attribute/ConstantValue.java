package com.mcwcapsule.VJVM.runtime.metadata.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class ConstantValue extends Attribute {
    @NonNull
    @Getter
    private final Object value;
}
