package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawStringConstant extends UnevaluatedConstant {
    private final int stringIndex;

    @Override
    public StringConstant evaluate(ConstantPool constantPool) {
        return new StringConstant(((UTF8Constant) constantPool.getConstant(stringIndex)).getValue());
    }

}
