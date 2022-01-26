package vjvm.runtime.classdata.constant;

import vjvm.runtime.object.StringObject;
import vjvm.vm.VMContext;

public class StringConstant extends ValueConstant {
    private int addr = 0;

    public StringConstant(String value) {
        super(value);
    }

    public String string() {
        return (String) value;
    }

    @Override
    public Integer value(VMContext context) {
        assert context != null;

        if (addr == 0) {
            addr = context.heap().intern(new StringObject((String) value, context));
        }
        return addr;
    }
}
