package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawClassRef extends UnevaluatedConstant {
    private final int nameIndex;

    @Override
    public ClassRef evaluate(ConstantPool constantPool) {
        return new ClassRef(((UTF8Constant) constantPool.getConstant(nameIndex)).getValue());
    }

}
