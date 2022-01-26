package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LDC2_W extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var stack = frame.stack();
        var index = thread.pc().ushort();
        var value = frame.link().constant(index).value();

        // only long and double are supported
        if (value instanceof Long)
            stack.pushLong((Long) value);
        else if (value instanceof Double)
            stack.pushDouble((Double) value);
    }

}
