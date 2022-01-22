package vjvm.utils;

import vjvm.runtime.JClass;
import vjvm.vm.VMContext;

public class StringUtil {

    public static String valueOf(int str, VMContext ctx) {
        JClass stringClass;
        JClass charArrayClass;
        int arrOffset;

        stringClass = ctx.bootstrapLoader().loadClass("java/lang/String");
        charArrayClass = ctx.bootstrapLoader().loadClass("[C");
        arrOffset = stringClass.findField("value", "[C").offset();

        var slots = ctx.heap().slots();
        var arr = slots.int_(str + arrOffset);
        var len = slots.int_(arr + charArrayClass.instanceSize() - 1);
        var v = new char[len];
        for (int i = 0; i < len; ++i)
            v[i] = ArrayUtil.getChar(arr, i, ctx.heap());
        return new String(v);
    }

    /**
     * Create an intern string from value.
     * The impl assumes that a string object contains a byte[] named value.
     *
     * @param value value of the string
     * @param ctx JVM context
     * @return address of the created string
     */
    public static int createString(String value, VMContext ctx) {
        JClass stringClass;
        JClass charArrayClass;
        int arrOffset;

        stringClass = ctx.bootstrapLoader().loadClass("java/lang/String");
        charArrayClass = ctx.bootstrapLoader().loadClass("[B");
        arrOffset = stringClass.findField("value", "[B").offset();

        var str = stringClass.createInstance();
        var arr = ArrayUtil.newInstance(charArrayClass, value.length(), ctx.heap());
        var slots = ctx.heap().slots();

        // fill the char array
        for (int i = 0; i < value.length(); ++i)
            ArrayUtil.setChar(arr, i, value.charAt(i), ctx.heap());

        slots.addressAt(str + arrOffset, arr);
        return str;
    }
}
