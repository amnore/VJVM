package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

import lombok.Getter;

public class FieldRef extends Constant implements ResolvableConstant {
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
    public void resolve(JClass jClass) {
        // TODO: resolve
    }
}
