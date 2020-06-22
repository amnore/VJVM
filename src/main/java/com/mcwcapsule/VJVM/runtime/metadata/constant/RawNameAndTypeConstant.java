package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawNameAndTypeConstant extends UnevaluatedConstant {
    private final int nameIndex;
    private final int descriptorIndex;

    @Override
    public NameAndTypeConstant evaluate(RuntimeConstantPool constantPool) {
        return new NameAndTypeConstant(((UTF8Constant) constantPool.getConstant(nameIndex)).getValue(),
            ((UTF8Constant) constantPool.getConstant(descriptorIndex)).getValue());
    }
}
