package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.classdata.constant.FieldRef;
import lombok.val;

public class GETSTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        FieldInfo field;
        JClass jClass;
        try {
            val ref = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
            ref.resolve(frame.getJClass());
            field = ref.getInfo();
            jClass = ref.getClassRef().getJClass();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (jClass.getInitState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        if (field.getSize() == 2)
            stack.pushLong(jClass.getStaticFields().getLong(field.getOffset()));
        else
            stack.pushInt(jClass.getStaticFields().getInt(field.getOffset()));
    }

}
