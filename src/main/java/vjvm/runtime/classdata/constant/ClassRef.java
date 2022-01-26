package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import vjvm.runtime.JClass;

import java.io.DataInput;

public class ClassRef extends Constant {
    private final int index;
    private final JClass self;

    private String name;
    private JClass ref;

    @SneakyThrows
    ClassRef(DataInput input, JClass thisClass) {
        index = input.readUnsignedShort();
        this.self = thisClass;
    }

    public ClassRef(String name, JClass thisClass) {
        this.name = name;
        this.self = thisClass;
        this.index = 0;
    }

    @Override
    public JClass value() {
        if (ref != null) {
            return ref;
        }

        // check whether the reference points to this class
        if (name().equals(self.thisClass().name())) {
            ref = self;
        } else {
            // if not, load the Class using the defining class loader of this class
            ref = self.classLoader().loadClass(name());
        }

        if (!ref.accessibleTo(self))
            throw new IllegalAccessError();

        return ref;
    }

    public String name() {
        if (name == null) {
            name = ((UTF8Constant) self.constantPool().constant(index)).value();
        }
        return name;
    }
}
