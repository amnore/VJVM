package vjvm.runtime.classdata.constant;

import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class DoubleConstant extends ValueConstant {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public Double value(VMContext context) {
        return (Double) value;
    }
}
