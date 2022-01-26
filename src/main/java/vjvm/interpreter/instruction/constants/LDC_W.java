package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ValueConstant;

public class LDC_W extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var index = thread.pc().ushort();
        var value = ((ValueConstant) frame.dynLink().constant(index)).value(thread.context());

        // only int and float are supported
        if (value instanceof Integer)
            stack.pushInt((Integer) value);
        else if (value instanceof Float)
            stack.pushFloat((Float) value);
    }

}
