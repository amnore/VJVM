package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.classdata.constant.FieldRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class GETFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val obj = stack.popAddress();
        val ref = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        if (obj == 0)
            throw new NullPointerException();
        FieldInfo field;
        try {
            ref.resolve(frame.getJClass());
            field = ref.getInfo();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val slots = VJVM.getHeap().getSlots();
        if (field.getSize() == 2)
            stack.pushLong(slots.getLong(obj + field.getOffset()));
        else stack.pushInt(slots.getInt(obj + field.getOffset()));
    }

}
