package vjvm.runtime.classdata.constant;

import vjvm.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawMethodRef extends UnevaluatedConstant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    @Override
    public MethodRef evaluate(ConstantPool constantPool) {
        var ref = constantPool.constant(classIndex);
        if (ref instanceof UnevaluatedConstant) {
            ref = ((UnevaluatedConstant) ref).evaluate(constantPool);
            constantPool.constant(classIndex, ref);
        }
        var nameAndType = constantPool.constant(nameAndTypeIndex);
        if (nameAndType instanceof UnevaluatedConstant) {
            nameAndType = ((UnevaluatedConstant) nameAndType).evaluate(constantPool);
            constantPool.constant(nameAndTypeIndex, nameAndType);
        }
        return new MethodRef((ClassRef) ref, ((NameAndTypeConstant) nameAndType).name(),
            ((NameAndTypeConstant) nameAndType).descriptor(), false, constantPool.jClass());
    }
}
