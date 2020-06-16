package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

import lombok.Getter;
import lombok.NonNull;

public class ClassRef implements ResolvableConstant {
    private final int nameIndex;
    @Getter
    String name;

    @Getter
    JClass jClass;

    public ClassRef(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    @Override
    public void resolve(RuntimeConstantPool constantPool) {
        // TODO: resolve
    }
}
