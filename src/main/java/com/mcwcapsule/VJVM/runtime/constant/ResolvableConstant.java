package com.mcwcapsule.VJVM.runtime.constant;

import com.mcwcapsule.VJVM.runtime.RuntimeConstantPool;

public interface ResolvableConstant {
    public abstract void resolve(RuntimeConstantPool constantPool);
}
