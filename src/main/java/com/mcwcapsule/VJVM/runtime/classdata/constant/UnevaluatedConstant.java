package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.classdata.ConstantPool;

/**
 * These are constants constructed directly from binary data and requires further evaluation.
 */
public abstract class UnevaluatedConstant extends Constant {
    public abstract Constant evaluate(ConstantPool constantPool);
}
