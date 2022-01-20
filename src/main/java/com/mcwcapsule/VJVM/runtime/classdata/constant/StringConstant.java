package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.utils.StringUtil;
import com.mcwcapsule.VJVM.vm.VJVM;

public class StringConstant extends ValueConstant {
    int strAddr;

    public StringConstant(String value) {
        super(value);
    }

    public String getString() {
        return (String) value;
    }

    @Override
    public Integer getValue() {
        return strAddr == 0 ? (strAddr = VJVM.getHeap().getInternString(
            StringUtil.createString((String) value))) : strAddr;
    }

}
