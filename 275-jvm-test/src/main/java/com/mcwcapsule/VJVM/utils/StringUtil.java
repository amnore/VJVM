package com.mcwcapsule.VJVM.utils;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class StringUtil {

    public static String valueOf(int str) {
        JClass stringClass;
        JClass charArrayClass;
        int arrOffset;
        try {
            stringClass = VJVM.getBootstrapLoader().loadClass("java/lang/String");
            charArrayClass = VJVM.getBootstrapLoader().loadClass("[C");
            arrOffset = stringClass.findField("value", "[C").getOffset();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val slots = VJVM.getHeap().getSlots();
        val arr = slots.getInt(str + arrOffset);
        val len = slots.getInt(arr + charArrayClass.getInstanceSize() - 1);
        val v = new char[len];
        for (int i = 0; i < len; ++i)
            v[i] = ArrayUtil.getChar(arr, i);
        return new String(v);
    }

    /**
     * Create an intern string from value.
     * The impl assumes that a string object contains a char[] named value.
     *
     * @param value value of the string
     * @return address of the created string
     */
    public static int createString(String value) {
        JClass stringClass;
        JClass charArrayClass;
        int arrOffset;
        try {
            stringClass = VJVM.getBootstrapLoader().loadClass("java/lang/String");
            charArrayClass = VJVM.getBootstrapLoader().loadClass("[C");
            arrOffset = stringClass.findField("value", "[C").getOffset();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val str = stringClass.createInstance();
        val arr = ArrayUtil.newInstance(charArrayClass, value.length());
        val slots = VJVM.getHeap().getSlots();

        // fill the char array
        for (int i = 0; i < value.length(); ++i)
            ArrayUtil.setChar(arr, i, value.charAt(i));

        slots.setAddress(str + arrOffset, arr);
        return str;
    }
}
