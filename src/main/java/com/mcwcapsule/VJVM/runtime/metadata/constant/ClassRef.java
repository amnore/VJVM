package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

import lombok.Getter;

public class ClassRef extends Constant implements ResolvableConstant {
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
