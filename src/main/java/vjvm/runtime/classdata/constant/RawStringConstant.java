package vjvm.runtime.classdata.constant;

import vjvm.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawStringConstant extends UnevaluatedConstant {
    private final int stringIndex;

    @Override
    public StringConstant evaluate(ConstantPool constantPool) {
        return new StringConstant(((UTF8Constant) constantPool.constant(stringIndex)).value(), constantPool.context());
    }

}
