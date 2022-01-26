package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class AIFALOAD extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var index = stack.popInt();
        var obj = thread.context().heap().get(stack.popAddress());

        assert obj.type().array();
        stack.pushInt(((ArrayObject) obj).int_(index));
    }
}
