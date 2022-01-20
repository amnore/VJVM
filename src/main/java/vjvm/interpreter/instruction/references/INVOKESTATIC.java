package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;
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
