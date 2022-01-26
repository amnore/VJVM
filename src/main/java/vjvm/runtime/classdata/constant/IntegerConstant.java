package vjvm.runtime.classdata.constant;

import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class IntegerConstant extends ValueConstant {
    public IntegerConstant(Integer value) {
        super(value);
    }

    @Override
    public Integer value(VMContext context) {
        return (Integer) value;
    }
}
