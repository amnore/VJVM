package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.ArrayObject;

public class ANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var stack = frame.stack();
        var count = stack.popInt();
        var ref = (ClassRef) frame.link().constant(thread.pc().ushort());

        JClass arrayClass = ref.jClass().classLoader().loadClass("[L" + ref.name() + ';');
        arrayClass.initialize(thread);
        assert arrayClass.initState() == JClass.InitState.INITIALIZED;

        var arr = new ArrayObject(arrayClass, count);
        stack.pushAddress(arr.address());
    }

}
