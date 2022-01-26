package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.vm.VMContext;

public class CHECKCAST extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var classRef = (ClassRef) frame.dynLink().constant(thread.pc().ushort());
        var obj = stack.popAddress();
        if (obj == 0) {
            stack.pushAddress(obj);
            return;
        }

        var jClass = classRef.jClass();
        var objClass = thread.context().heap().get(obj).type();
        System.err.println(jClass.thisClass().name());
        System.err.println(objClass.thisClass().name());
        if (!objClass.castableTo(jClass))
            throw new ClassCastException();
        stack.pushAddress(obj);
    }
}
