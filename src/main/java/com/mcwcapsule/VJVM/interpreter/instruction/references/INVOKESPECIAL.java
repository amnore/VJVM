package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.CallUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class INVOKESPECIAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        val heap = VJVM.getHeap();
        val opSlots = frame.getOpStack().getSlots();
        val argc = methodRef.getArgc();
        val currentClass = heap.getJClass(opSlots.getInt(frame.getOpStack().getTop() - argc - 1));
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        JClass targetClass;
        if (!methodRef.getName().equals("<init>") && !methodRef.getJClass().isInterface() && methodRef.getJClass().isSuper())
            targetClass = currentClass.getSuperClass().getJClass();
        else targetClass = methodRef.getJClass();
        val method = targetClass.findMethod(methodRef.getName(), methodRef.getDescriptor());
        CallUtil.callMethod(targetClass, method, thread);
    }

}
