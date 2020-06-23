package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.metadata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.CallUtil;
import lombok.val;

public class INVOKESTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (methodRef.getJClass().getInitState() != JClass.InitState.INITIALIZED) {
            thread.getPC().move(-3);
            methodRef.getJClass().initialize(thread);
            return;
        }
        CallUtil.callMethod(methodRef.getJClass(), methodRef.getInfo(), thread);
    }

}
