package vjvm.runtime.classdata.constant;

import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class FloatConstant extends ValueConstant {
    public FloatConstant(Float value) {
        super(value);
    }

    @Override
    public Float value(VMContext context) {
        return (Float) value;
    }
}
