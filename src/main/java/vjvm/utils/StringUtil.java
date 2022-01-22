package vjvm.utils;

import vjvm.runtime.JClass;
import vjvm.vm.VMContext;

public class StringUtil {

    public static String valueOf(int str) {
        JClass stringClass;
        JClass charArrayClass;
        int arrOffset;

        stringClass = VMContext.bootstrapLoader().loadClass("java/lang/String");
        charArrayClass = VMContext.bootstrapLoader().loadClass("[C");
        arrOffset = stringClass.findField("value", "[C").offset();

        var slots = VMContext.heap().slots();
        var arr = slots.int_(str + arrOffset);
        var len = slots.int_(arr + charArrayClass.instanceSize() - 1);
        var v = new char[len];
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

        stringClass = VMContext.bootstrapLoader().loadClass("java/lang/String");
        charArrayClass = VMContext.bootstrapLoader().loadClass("[C");
        arrOffset = stringClass.findField("value", "[C").offset();

        var str = stringClass.createInstance();
        var arr = ArrayUtil.newInstance(charArrayClass, value.length());
        var slots = VMContext.heap().slots();

        // fill the char array
        for (int i = 0; i < value.length(); ++i)
            ArrayUtil.setChar(arr, i, value.charAt(i));

        slots.addressAt(str + arrOffset, arr);
        return str;
    }
}
