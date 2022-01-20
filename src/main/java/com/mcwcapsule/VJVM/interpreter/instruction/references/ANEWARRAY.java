package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.utils.ArrayUtil;
import lombok.val;

public class ANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val count = stack.popInt();
        val ref = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        JClass arrayClass;
        try {
            ref.resolve(frame.getJClass());
            arrayClass = ref.getJClass().getClassLoader().loadClass("[L" + ref.getName() + ';');
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (arrayClass.getInstanceSize() != JClass.InitState.INITIALIZED)
            arrayClass.tryInitialize(thread);
        val arr = ArrayUtil.newInstance(arrayClass, count);
        stack.pushAddress(arr);
    }

}
