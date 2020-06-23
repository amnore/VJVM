package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import lombok.val;

public class ANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val count = stack.popInt();
        val ref = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        ArrayClass arrayClass;
        try {
            ref.resolve(frame.getJClass());
            arrayClass = (ArrayClass) ref.getJClass().getClassLoader().loadClass('[' + ref.getName());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val arr = arrayClass.createInstance(count);
        stack.pushAddress(arr);
    }

}
