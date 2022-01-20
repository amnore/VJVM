package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ValueConstant;
import lombok.val;

public class LDC2_W extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val index = thread.getPC().getUnsignedShort();
        val value = ((ValueConstant) frame.getDynLink().getConstant(index)).getValue();

        // only long and double are supported
        if (value instanceof Long)
            stack.pushLong((Long) value);
        else if (value instanceof Double)
            stack.pushDouble((Double) value);
    }

}
