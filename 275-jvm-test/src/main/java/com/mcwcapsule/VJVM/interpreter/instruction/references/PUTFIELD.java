package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.FieldRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;

public class PUTFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val fieldRef = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());

        // log
        System.err.println(fieldRef.getName());

        try {
            fieldRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val stack = frame.getOpStack();
        val field = fieldRef.getInfo();
        val slots = VJVM.getHeap().getSlots();
        if (fieldRef.getSize() == 1) {
            var value = stack.popInt();
            val ref = stack.popAddress();
            if (field.getDescriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            slots.setInt(ref + field.getOffset(), value);
        } else {
            val value = stack.popLong();
            val ref = stack.popAddress();
            slots.setLong(ref + field.getOffset(), value);
        }
    }

}
