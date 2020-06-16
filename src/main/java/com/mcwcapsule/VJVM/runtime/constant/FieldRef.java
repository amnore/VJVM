package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;

import lombok.Getter;
import lombok.NonNull;

public class FieldRef implements ResolvableConstant {
    private final int classIndex;
    @Getter
    private ClassRef classRef;

    private final int nameAndTypeIndex;
    @Getter
    private String name;
    @Getter
    private String descriptor;

    @Getter
    private FieldInfo info;

    public FieldRef(final int classIndex, final int nameAndTypeIndex) {
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    @Override
    public void resolve(final RuntimeConstantPool constantPool) {
        // TODO: resolve
    }
}
