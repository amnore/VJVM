package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;
import vjvm.vm.VMContext;

public class INVOKESPECIAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var methodRef = (MethodRef) frame.dynLink().constant(thread.pc().ushort());

        // log
        System.err.println(methodRef.name());

        var heap = VMContext.heap();
        var opSlots = frame.opStack().slots();
        var argc = methodRef.argc();
        var currentClass = frame.jClass();

        methodRef.resolve(frame.jClass());

        JClass targetClass;
        JClass refClass = methodRef.jClass();
        if (!methodRef.name().equals("<init>") && !refClass.interface_()
            && currentClass.subClassOf(refClass) && refClass.super_())
            targetClass = currentClass.superClass().jClass();
        else targetClass = methodRef.jClass();
//        var method = targetClass.findMethod(methodRef.name(), methodRef.descriptor());
        var method = targetClass.vtableMethod(methodRef.info().vtableIndex());
        InvokeUtil.invokeMethod(method, thread);
    }

}
