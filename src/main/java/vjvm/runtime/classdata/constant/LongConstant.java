package vjvm.runtime.classdata.constant;

import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class LongConstant extends ValueConstant {
    public LongConstant(Long value) {
        super(value);
    }

    @Override
    public Long value(VMContext context) {
        return (Long) value;
    }
}
