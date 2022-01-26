package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.object.StringObject;

import java.io.DataInput;

public class StringConstant extends Constant {
    private int address = 0;

    private final int stringIndex;
    private final JClass thisClass;

    @SneakyThrows
    StringConstant(DataInput input, JClass thisClass) {
        stringIndex = input.readUnsignedShort();
        this.thisClass = thisClass;
    }

    @Override
    public Integer value() {
        if (address == 0) {
            var value = ((UTF8Constant)thisClass.constantPool().constant(stringIndex)).value();
            var ctx = thisClass.context();
            address = ctx.heap().intern(new StringObject(value, ctx));
        }
        return address;
    }
}
