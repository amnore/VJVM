package com.mcwcapsule.VJVM.utils;

import com.mcwcapsule.VJVM.runtime.JFrame;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import lombok.val;
import lombok.var;

public class CallUtil {
    public static void callMethod(MethodInfo method, JThread thread) {
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
