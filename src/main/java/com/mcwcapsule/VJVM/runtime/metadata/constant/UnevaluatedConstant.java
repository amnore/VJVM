package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

/**
 * These are constants constructed directly from binary data and requires further evaluation.
 */
public abstract class UnevaluatedConstant extends Constant {
    public abstract Constant evaluate(RuntimeConstantPool constantPool);
}
