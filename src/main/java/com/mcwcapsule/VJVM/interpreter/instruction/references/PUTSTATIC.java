package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors;
import com.mcwcapsule.VJVM.runtime.metadata.constant.FieldRef;
import lombok.val;
import lombok.var;

public class PUTSTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val fieldRef = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            fieldRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = fieldRef.getJClass();
        if (jClass.getInitState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        val slots = jClass.getStaticFields();
        val field = fieldRef.getInfo();
        val stack = frame.getOpStack();
        if (fieldRef.getSize() == 2)
            slots.setLong(field.getOffset(), stack.popLong());
        else {
            var value = stack.popInt();
            if (fieldRef.getDescriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            slots.setInt(field.getOffset(), value);
        }
    }

}
