package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import lombok.val;

public class NEW extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val classRef = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            classRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = classRef.getJClass();
        if (jClass.getInitState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        frame.getOpStack().pushAddress(jClass.createInstance());
    }

}
