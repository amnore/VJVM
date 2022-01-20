package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;
import vjvm.vm.VJVM;
import lombok.val;

public class INVOKESPECIAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());

        // log
        System.err.println(methodRef.getName());

        val heap = VJVM.getHeap();
        val opSlots = frame.getOpStack().getSlots();
        val argc = methodRef.getArgc();
        val currentClass = frame.getJClass();
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        JClass targetClass;
        JClass refClass = methodRef.getJClass();
        if (!methodRef.getName().equals("<init>") && !refClass.isInterface()
            && currentClass.isSubClassOf(refClass) && refClass.isSuper())
            targetClass = currentClass.getSuperClass().getJClass();
        else targetClass = methodRef.getJClass();
//        val method = targetClass.findMethod(methodRef.getName(), methodRef.getDescriptor());
        val method = targetClass.getVtableMethod(methodRef.getInfo().getVtableIndex());
        InvokeUtil.invokeMethod(method, thread);
    }

}
