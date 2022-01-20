package vjvm.utils;

import vjvm.runtime.*;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.vm.VJVM;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.function.Consumer;

public class InvokeUtil {
    // (ClassName, MethodName, MethodDescriptor) -> HackFunction
    static HashMap<Triple<String, String, String>, Consumer<OperandStack>> hackTable;

    static {
        hackTable = new HashMap<>();
        hackTable.put(Triple.of("java/lang/Object", "registerNatives", "()V"), s -> {
        });
        hackTable.put(Triple.of("java/lang/Class", "registerNatives", "()V"), s -> {
        });
        hackTable.put(Triple.of("java/lang/Class", "desiredAssertionStatus0", "(Ljava/lang/Class;)Z"), s -> {
            s.popAddress();
            s.pushInt(1);
        });
        hackTable.put(Triple.of("java/lang/String", "intern", "()Ljava/lang/String;"),
            s -> s.pushAddress(VJVM.heap().internString(s.popAddress())));
        hackTable.put(Triple.of("cases/TestUtil", "reach", "(I)V"), s -> {
            System.out.println(s.popInt());
        });
        hackTable.put(Triple.of("cases/TestUtil", "equalInt", "(II)Z"), s -> {
            var right = s.popInt();
            var left = s.popInt();
            if (left != right)
                throw new TestUtilException(String.format("%d!=%d", left, right));
            else s.pushInt(1);
        });
        hackTable.put(Triple.of("cases/TestUtil", "equalFloat", "(FF)Z"), s -> {
            var right = s.popFloat();
            var left = s.popFloat();
            if (left != right)
                throw new TestUtilException(String.format("%f!=%f", left, right));
            else s.pushInt(1);
        });
        hackTable.put(Triple.of("java/lang/Throwable", "fillInStackTrace", "(I)Ljava/lang/Throwable;"), s -> {
            s.popInt();
        });
        hackTable.put(Triple.of("java/lang/Class", "getPrimitiveClass", "(Ljava/lang/String;)Ljava/lang/Class;"), s -> {
            s.pushAddress(JClass.primitiveClass(StringUtil.valueOf(s.popAddress())).classObject());
        });
        hackTable.put(Triple.of("java/lang/Float", "floatToRawIntBits", "(F)I"), s -> {
        });
        hackTable.put(Triple.of("java/lang/Double", "doubleToRawLongBits", "(D)J"), s -> {
        });
        hackTable.put(Triple.of("java/lang/Double", "longBitsToDouble", "(J)D"), s -> {
        });
        hackTable.put(Triple.of("java/lang/System", "registerNatives", "()V"), s -> {
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "sin", "(D)D"), s -> {
            s.pushDouble(Math.sin(s.popDouble()));
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "exp", "(D)D"), s -> {
            s.pushDouble(Math.exp(s.popDouble()));
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "pow", "(DD)D"), s -> {
            var right = s.popDouble();
            var left = s.popDouble();
            s.pushDouble(Math.pow(left, right));
        });
        hackTable.put(Triple.of("java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V"), s -> {
            // only support char[], no checks
            var length = s.popInt();
            var destPos = s.popInt();
            var dest = s.popAddress();
            var srcPos = s.popInt();
            var src = s.popAddress();
            for (int i = 0; i < length; ++i)
                ArrayUtil.setChar(dest, destPos + i, ArrayUtil.getChar(src, srcPos + i));
        });
    }

    public static void invokeMethod(MethodInfo method, JThread thread) {
        var t = Triple.of(method.jClass().thisClass().name(), method.name(), method.descriptor());
        var m = hackTable.get(t);
        if (m != null) {
            m.accept(thread.currentFrame().opStack());
            return;
        }

        if (method.native_())
            throw new Error("Unimplemented native method: " + t.toString());

        var stack = thread.currentFrame().opStack();
        var newFrame = new JFrame(method);
        var argSlots = method.argc();
        if (!method.static_())
            ++argSlots;

        stack.slots().copyTo(stack.top() - argSlots, argSlots, newFrame.localVars(), 0);
        stack.popSlots(argSlots);
        thread.pushFrame(newFrame);
    }

    /**
     * Invoke a method when there is no frames in a thread.
     *
     * @param method the method to call
     * @param thread the thread to run
     * @param args   the supplied arguments, index begins at 0, can be null if the method has no arguments
     */
    public static void invokeMethodWithArgs(MethodInfo method, JThread thread, Slots args) {
        var frame = new JFrame(method);
        var argc = method.argc();
        if (!method.static_())
            ++argc;
        if (argc != 0)
            args.copyTo(0, argc, frame.localVars(), 0);
        thread.pushFrame(new JFrame(method));
    }
}
