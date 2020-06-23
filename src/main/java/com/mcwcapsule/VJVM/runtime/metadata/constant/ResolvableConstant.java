package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public abstract class ResolvableConstant extends Constant {
    public abstract void resolve(JClass jClass) throws ClassNotFoundException;
}
