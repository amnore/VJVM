package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

public interface ResolvableConstant {
    public abstract void resolve(RuntimeConstantPool constantPool);
}
