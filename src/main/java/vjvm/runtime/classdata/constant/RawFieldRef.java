package vjvm.runtime.classdata.constant;

import vjvm.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawFieldRef extends UnevaluatedConstant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    @Override
    public FieldRef evaluate(ConstantPool constantPool) {
        var cr = constantPool.constant(classIndex);
        if (cr instanceof UnevaluatedConstant) {
            cr = ((UnevaluatedConstant) cr).evaluate(constantPool);
            constantPool.constant(classIndex, cr);
        }
        var nt = constantPool.constant(nameAndTypeIndex);
        if (nt instanceof UnevaluatedConstant) {
            nt = ((UnevaluatedConstant) nt).evaluate(constantPool);
            constantPool.constant(nameAndTypeIndex, nt);
        }
        return new FieldRef(constantPool.jClass(), (ClassRef) cr, ((NameAndTypeConstant) nt).name(),
            ((NameAndTypeConstant) nt).descriptor());
    }

}
