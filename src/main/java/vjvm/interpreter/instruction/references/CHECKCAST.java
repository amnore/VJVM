package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;

public class CHECKCAST extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var stack = frame.stack();
        var classRef = (ClassRef) frame.link().constant(thread.pc().ushort());
        var obj = stack.popAddress();
        if (obj == 0) {
            stack.pushAddress(obj);
            return;
        }

        var jClass = classRef.value();
        var objClass = thread.context().heap().get(obj).type();
        if (!objClass.castableTo(jClass))
            throw new ClassCastException();
        stack.pushAddress(obj);
    }
}
