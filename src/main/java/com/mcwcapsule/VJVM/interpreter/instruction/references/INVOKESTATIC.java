package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.InvokeUtil;
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
        if (methodRef.getJClass().getInitState() != JClass.InitState.INITIALIZED)
            methodRef.getJClass().tryInitialize(thread);
        InvokeUtil.invokeMethod(methodRef.getInfo(), thread);
    }

}
