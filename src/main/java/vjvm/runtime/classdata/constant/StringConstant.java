package vjvm.runtime.classdata.constant;

import vjvm.utils.StringUtil;
import vjvm.vm.VJVM;

public class StringConstant extends ValueConstant {
    int strAddr;

    public StringConstant(String value) {
        super(value);
    }

    public String string() {
        return (String) value;
    }

    @Override
    public Integer value() {
        return strAddr == 0 ? (strAddr = VJVM.heap().internString(
            StringUtil.createString((String) value))) : strAddr;
    }

}
