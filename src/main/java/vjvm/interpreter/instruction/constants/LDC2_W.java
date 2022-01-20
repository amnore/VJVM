package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ValueConstant;

public class LDC2_W extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var index = thread.pc().ushort();
        var value = ((ValueConstant) frame.dynLink().constant(index)).value();

        // only long and double are supported
        if (value instanceof Long)
            stack.pushLong((Long) value);
        else if (value instanceof Double)
            stack.pushDouble((Double) value);
    }

}
