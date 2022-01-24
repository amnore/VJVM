package vjvm.runtime.classdata.constant;

import vjvm.runtime.JHeap;
import vjvm.utils.StringUtil;
import vjvm.vm.VMContext;

public class StringConstant extends ValueConstant {
    private int strAddr = 0;
    private final VMContext context;

    public StringConstant(String value, VMContext ctx) {
        super(value);
        context = ctx;
    }

    public String string() {
        return (String) value;
    }

    @Override
    public Integer value() {
        return strAddr != 0 ? strAddr :
            (strAddr = context.heap().internString(StringUtil.createString((String) value, context)));
    }
}
