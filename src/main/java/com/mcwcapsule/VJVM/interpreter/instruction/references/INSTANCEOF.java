package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class INSTANCEOF extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val classRef = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        val obj = stack.popAddress();
        if (obj == 0)
            stack.pushInt(0);
        try {
            classRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = classRef.getJClass();
        val objClass = VJVM.getHeap().getJClass(VJVM.getHeap().getSlots().getInt(obj - 1));
        // only non-array class are considered
        if (objClass.getThisClass().getName().charAt(0) != '[')
            // interfaces are not considered
            stack.pushInt(objClass.isSubclassOf(jClass) ? 1 : 0);
        else stack.pushInt(0);
    }

}
