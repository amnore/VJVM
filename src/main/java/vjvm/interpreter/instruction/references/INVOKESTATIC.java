package vjvm.interpreter.instruction.references;

import vjvm.interpreter.JInterpreter;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var methodRef = (MethodRef) frame.link().constant(thread.pc().ushort());
        methodRef.jClass().initialize(thread);
        assert methodRef.jClass().initState() == JClass.InitState.INITIALIZED;

        var args = frame.stack().popSlots(methodRef.argc());
        JInterpreter.invokeMethodWithArgs(methodRef.info(), thread, args);
    }

}
