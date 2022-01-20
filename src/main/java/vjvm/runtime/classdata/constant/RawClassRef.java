package vjvm.runtime.classdata.constant;

import vjvm.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawClassRef extends UnevaluatedConstant {
    private final int nameIndex;

    @Override
    public ClassRef evaluate(ConstantPool constantPool) {
        return new ClassRef(((UTF8Constant) constantPool.constant(nameIndex)).value());
    }

}
