package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

import lombok.Getter;

public class NameAndTypeConstant extends Constant implements ResolvableConstant {
    private final int nameIndex;
    @Getter
    private String name;
    private final int descriptorIndex;
    @Getter
    private String descriptor;

    public NameAndTypeConstant(int nameIndex, int descriptorIndex) {
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    @Override
    public void resolve(JClass jClass) {
        // TODO: resolve
    }
}
