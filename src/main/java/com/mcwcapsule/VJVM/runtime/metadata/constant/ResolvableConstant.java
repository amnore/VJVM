package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;

public interface ResolvableConstant {
    void resolve(JClass jClass) throws ClassNotFoundException;
}
