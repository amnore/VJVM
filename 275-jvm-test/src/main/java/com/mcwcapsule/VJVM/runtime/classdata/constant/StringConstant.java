package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.NonArrayClass;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

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
        return strAddr == 0 ? (strAddr = createInternString()) : strAddr;
    }

    /**
     * Create an intern string from value.
     * The impl assumes that a string object contains a char[] named value.
     *
     * @return address of the created string
     */
    private int createInternString() {
        NonArrayClass strC;
        ArrayClass arrC;
        try {
            strC = (NonArrayClass) VJVM.getBootstrapLoader().loadClass("java/lang/String");
            arrC = (ArrayClass) VJVM.getBootstrapLoader().loadClass("[C");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val s = (String) value;
        val str = strC.createInstance();
        val arr = arrC.createInstance(s.length());
        val slots = VJVM.getHeap().getSlots();

        // fill the char array
        for (int i = 0; i < s.length(); i += 2) {
            int v = ((int) s.charAt(i)) << 16;
            if (i != s.length() - 1)
                v |= s.charAt(i + 1);
            slots.setInt(arr + arrC.getInstanceSize() + i / 2, v);
        }

        slots.setAddress(str + strC.findField("value", "[C").getOffset(), arr);
        return VJVM.getHeap().getInternString(str);
    }
}
