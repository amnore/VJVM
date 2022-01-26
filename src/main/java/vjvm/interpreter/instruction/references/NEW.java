package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.JObject;

public class NEW extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var classRef = (ClassRef) frame.link().constant(thread.pc().ushort());

        var jClass = classRef.value();
        jClass.initialize(thread);
        assert jClass.initState() == JClass.InitState.INITIALIZED;

        var obj = new JObject(jClass);
        frame.stack().pushAddress(obj.address());
    }

}
