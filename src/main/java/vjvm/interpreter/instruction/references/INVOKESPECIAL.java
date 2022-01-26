package vjvm.interpreter.instruction.references;

import vjvm.interpreter.JInterpreter;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESPECIAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var methodRef = (MethodRef) frame.link().constant(thread.pc().ushort());
        var currentClass = frame.jClass();

        JClass targetClass;
        JClass refClass = methodRef.jClass();
        if (!methodRef.name().equals("<init>") && !refClass.interface_()
            && currentClass.subClassOf(refClass) && refClass.super_())
            targetClass = currentClass.superClass().jClass();
        else targetClass = methodRef.jClass();
//        var method = targetClass.findMethod(methodRef.name(), methodRef.descriptor());
        var args = frame.stack().popSlots(methodRef.argc() + 1);
        var method = targetClass.vtableMethod(methodRef.info().vtableIndex());
        JInterpreter.invokeMethodWithArgs(method, thread, args);
    }

}
