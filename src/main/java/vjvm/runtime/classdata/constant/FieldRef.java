package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;

import java.io.DataInput;

public class FieldRef extends Constant {
    private final int classIndex;
    private final int nameAndTypeIndex;
    private final JClass self;

    private FieldInfo field;

    @SneakyThrows
    FieldRef(DataInput input, JClass self) {
        classIndex = input.readUnsignedShort();
        nameAndTypeIndex = input.readUnsignedShort();
        this.self = self;
    }

    @Override
    public FieldInfo value() {
        if (field != null) {
            return field;
        }

        /*
          Resolves field reference. See spec. 5.4.3.2
         */
        var pool = self.constantPool();
        var ref = ((ClassRef) pool.constant(classIndex)).value();
        var pair = ((NameAndTypeConstant) pool.constant(nameAndTypeIndex)).value();

        field = ref.findField(pair.getLeft(), pair.getRight());
        if (field == null)
            throw new NoSuchFieldError();
        if (!field.accessibleTo(self, ref))
            throw new IllegalAccessError();

        return field;
    }
}
