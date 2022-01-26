package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class ARRAYLENGTH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.top().stack();
        var obj = thread.context().heap().get(stack.popAddress());

        assert obj.type().array();
        stack.pushInt(((ArrayObject)obj).length());
    }

}
