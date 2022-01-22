package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;

public class NEW extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var classRef = (ClassRef) frame.dynLink().constant(thread.pc().ushort());
        classRef.resolve(frame.jClass());

        var jClass = classRef.jClass();
        if (jClass.initState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        frame.opStack().pushAddress(jClass.createInstance());
    }

}
