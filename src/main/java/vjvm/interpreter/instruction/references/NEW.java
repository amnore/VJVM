package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
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
