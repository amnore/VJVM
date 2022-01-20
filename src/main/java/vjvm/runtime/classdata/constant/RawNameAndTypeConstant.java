package vjvm.runtime.classdata.constant;

import vjvm.runtime.classdata.ConstantPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawNameAndTypeConstant extends UnevaluatedConstant {
    private final int nameIndex;
    private final int descriptorIndex;

    @Override
    public NameAndTypeConstant evaluate(ConstantPool constantPool) {
        return new NameAndTypeConstant(((UTF8Constant) constantPool.getConstant(nameIndex)).getValue(),
            ((UTF8Constant) constantPool.getConstant(descriptorIndex)).getValue());
    }
}
