package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.JObject;

public class NEW extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var classRef = (ClassRef) frame.dynLink().constant(thread.pc().ushort());

        var jClass = classRef.jClass();
        jClass.initialize(thread);
        assert jClass.initState() == JClass.InitState.INITIALIZED;

        var obj = new JObject(jClass);
        frame.opStack().pushAddress(obj.address());
    }

}
