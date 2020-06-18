package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

import lombok.RequiredArgsConstructor;
import lombok.var;

@RequiredArgsConstructor
public class RawInterfaceMethodRef extends UnevaluatedConstant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    @Override
    public InterfaceMethodRef evaluate(RuntimeConstantPool constantPool) {
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
        return new InterfaceMethodRef((ClassRef) cr, ((NameAndTypeConstant) nt).getName(),
                ((NameAndTypeConstant) nt).getDescriptor());
    }
}
