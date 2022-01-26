package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import vjvm.runtime.JClass;

import java.io.DataInput;

public class NameAndTypeConstant extends Constant {
    private String name;
    private String descriptor;

    private final int nameIndex;
    private final int descriptorIndex;
    private final JClass self;

    @SneakyThrows
    NameAndTypeConstant(DataInput input, JClass self) {
        nameIndex = input.readUnsignedShort();
        descriptorIndex = input.readUnsignedShort();
        this.self = self;
    }

    @Override
    public Pair<String, String> value() {
        if (name == null) {
            var pool = self.constantPool();
            name = ((UTF8Constant)pool.constant(nameIndex)).value();
            descriptor = ((UTF8Constant)pool.constant(descriptorIndex)).value();
        }

        return Pair.of(name, descriptor);
    }
}
