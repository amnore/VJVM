package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawInterfaceMethodRef extends UnevaluatedConstant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    @Override
    public MethodRef evaluate(ConstantPool constantPool) {
        var cr = constantPool.getConstant(classIndex);
        if (cr instanceof UnevaluatedConstant) {
            cr = ((UnevaluatedConstant) cr).evaluate(constantPool);
            constantPool.setConstant(classIndex, cr);
        }
        var nt = constantPool.getConstant(nameAndTypeIndex);
        if (nt instanceof UnevaluatedConstant) {
            nt = ((UnevaluatedConstant) nt).evaluate(constantPool);
            constantPool.setConstant(nameAndTypeIndex, nt);
        }
        return new MethodRef((ClassRef) cr, ((NameAndTypeConstant) nt).getName(),
            ((NameAndTypeConstant) nt).getDescriptor(), true);
    }
}
