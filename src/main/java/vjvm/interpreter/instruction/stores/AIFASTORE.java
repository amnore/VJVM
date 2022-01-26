package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class AIFASTORE extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.top().stack();
        var value = stack.popInt();
        var index = stack.popInt();
        var obj = thread.context().heap().get(stack.popAddress());

        assert obj.type().array();
        ((ArrayObject)obj).int_(index, value);
    }
}
