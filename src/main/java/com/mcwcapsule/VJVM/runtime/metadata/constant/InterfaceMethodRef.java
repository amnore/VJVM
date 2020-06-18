package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InterfaceMethodRef extends Constant implements ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private FieldInfo info;

    @Override
    public void resolve(JClass jClass) {
        // TODO: resolve
    }
}
