package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

public interface ResolvableConstant {
    public abstract void resolve(JClass jClass);
}
