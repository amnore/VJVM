package com.mcwcapsule.VJVM.utils;

import com.mcwcapsule.VJVM.runtime.JFrame;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.OperandStack;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.function.Consumer;

public class CallUtil {
    // (ClassName, MethodName, MethodDescriptor) -> HackFunction
    static HashMap<Triple<String, String, String>, Consumer<OperandStack>> nativeHacks;

    static {
        nativeHacks = new HashMap<>();
        nativeHacks.put(Triple.of("java/lang/Object", "registerNatives", "()V"), s -> {
        });
        final String tu = "testsource/TestUtil";
        final String mn = "assertEquals";
        nativeHacks.put(Triple.of(tu, mn, "(JJ)V"), s -> {
            assert s.popLong() == s.popLong();
        });
        nativeHacks.put(Triple.of(tu, mn, "(DD)V"), s -> {
            assert s.popDouble() == s.popDouble();
        });
        nativeHacks.put(Triple.of(tu, mn, "(ZZ)V"), s -> {
            assert s.popInt() == s.popInt();
        });
    }

    public static void callMethod(MethodInfo method, JThread thread) {
        // Hack: TestUtil, Object::<clinit>
        if (method.isNative()) {
            nativeHacks.get(Triple.of(method.getJClass().getThisClass().getName(), method.getName(), method.getDescriptor())).accept(thread.getCurrentFrame().getOpStack());
            return;
        }

        val stack = thread.getCurrentFrame().getOpStack();
        val newFrame = new JFrame(method);
        var argSlots = method.getArgc();
        if (!method.isStatic())
            ++argSlots;

        stack.getSlots().copyTo(stack.getTop() - argSlots, argSlots, newFrame.getLocalVars(), 0);
        stack.popSlots(argSlots);
        thread.pushFrame(newFrame);
    }
}
