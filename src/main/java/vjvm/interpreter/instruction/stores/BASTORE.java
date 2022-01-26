package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class BASTORE extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var value = stack.popInt();
        var index = stack.popInt();
        var obj = thread.context().heap().get(stack.popAddress());

        assert obj.type().array();
        ((ArrayObject)obj).byte_(index, (byte)value);
    }
}
