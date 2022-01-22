package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.vm.VMContext;

public class INSTANCEOF extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var classRef = (ClassRef) frame.dynLink().constant(thread.pc().ushort());
        var obj = stack.popAddress();
        if (obj == 0) {
            stack.pushInt(0);
            return;
        }

        var jClass = classRef.jClass();
        var objClass = thread.context().heap().jClass(thread.context().heap().slots().int_(obj - 1));
        System.err.println(jClass.thisClass().name());
        System.err.println(objClass.thisClass().name());
        stack.pushInt(objClass.castableTo(jClass) ? 1 : 0);
    }

}
