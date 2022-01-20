package com.mcwcapsule.VJVM.utils;

import com.mcwcapsule.VJVM.runtime.*;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;
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
            s -> s.pushAddress(VJVM.getHeap().getInternString(s.popAddress())));
        hackTable.put(Triple.of("cases/TestUtil", "reach", "(I)V"), s -> {
            System.out.println(s.popInt());
        });
        hackTable.put(Triple.of("cases/TestUtil", "equalInt", "(II)Z"), s -> {
            val right = s.popInt();
            val left = s.popInt();
            if (left != right)
                throw new TestUtilException(String.format("%d!=%d", left, right));
            else s.pushInt(1);
        });
        hackTable.put(Triple.of("cases/TestUtil", "equalFloat", "(FF)Z"), s -> {
            val right = s.popFloat();
            val left = s.popFloat();
            if (left != right)
                throw new TestUtilException(String.format("%f!=%f", left, right));
            else s.pushInt(1);
        });
        hackTable.put(Triple.of("java/lang/Throwable", "fillInStackTrace", "(I)Ljava/lang/Throwable;"), s -> {
            s.popInt();
        });
        hackTable.put(Triple.of("java/lang/Class", "getPrimitiveClass", "(Ljava/lang/String;)Ljava/lang/Class;"), s -> {
            s.pushAddress(JClass.getPrimitiveClass(StringUtil.valueOf(s.popAddress())).getClassObject());
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
            val right = s.popDouble();
            val left = s.popDouble();
            s.pushDouble(Math.pow(left, right));
        });
        hackTable.put(Triple.of("java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V"), s -> {
            // only support char[], no checks
            val length = s.popInt();
            val destPos = s.popInt();
            val dest = s.popAddress();
            val srcPos = s.popInt();
            val src = s.popAddress();
            for (int i = 0; i < length; ++i)
                ArrayUtil.setChar(dest, destPos + i, ArrayUtil.getChar(src, srcPos + i));
        });
    }

    public static void invokeMethod(MethodInfo method, JThread thread) {
        val t = Triple.of(method.getJClass().getThisClass().getName(), method.getName(), method.getDescriptor());
        val m = hackTable.get(t);
        if (m != null) {
            m.accept(thread.getCurrentFrame().getOpStack());
            return;
        }

        if (method.isNative())
            throw new Error("Unimplemented native method: " + t.toString());

        val stack = thread.getCurrentFrame().getOpStack();
        val newFrame = new JFrame(method);
        var argSlots = method.getArgc();
        if (!method.isStatic())
            ++argSlots;

        stack.getSlots().copyTo(stack.getTop() - argSlots, argSlots, newFrame.getLocalVars(), 0);
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
        val frame = new JFrame(method);
        var argc = method.getArgc();
        if (!method.isStatic())
            ++argc;
        if (argc != 0)
            args.copyTo(0, argc, frame.getLocalVars(), 0);
        thread.pushFrame(new JFrame(method));
    }
}
