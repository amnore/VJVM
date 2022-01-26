package vjvm.runtime.classdata.constant;

import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public abstract class ValueConstant extends Constant {
    protected final Object value;

    public ValueConstant(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public abstract Object value(VMContext context);
}
