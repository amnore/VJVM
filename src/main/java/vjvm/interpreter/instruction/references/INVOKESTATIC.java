package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;

public class INVOKESTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var methodRef = (MethodRef) frame.dynLink().constant(thread.pc().ushort());
        methodRef.resolve(frame.jClass());

        if (methodRef.jClass().initState() != JClass.InitState.INITIALIZED)
            methodRef.jClass().tryInitialize(thread);
        InvokeUtil.invokeMethod(methodRef.info(), thread);
    }

}
