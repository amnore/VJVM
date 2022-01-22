package vjvm.runtime.classdata.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.ValueConstant;

import java.io.DataInput;

public class ConstantValue extends Attribute {
    @NonNull
    @Getter
    private final Object value;

    @SneakyThrows
    public ConstantValue(DataInput input, ConstantPool constantPool) {
        int valueIndex = input.readUnsignedShort();
        value = ((ValueConstant) constantPool.constant(valueIndex)).value();
    }
}
