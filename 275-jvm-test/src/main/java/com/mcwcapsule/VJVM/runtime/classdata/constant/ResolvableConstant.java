package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import lombok.Getter;

@Getter
public abstract class ResolvableConstant extends Constant {
    public abstract void resolve(JClass jClass) throws ClassNotFoundException;
}
