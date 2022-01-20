package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ValueConstant;
import lombok.val;

public class LDC_W extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val index = thread.getPC().getUnsignedShort();
        val value = ((ValueConstant) frame.getDynLink().getConstant(index)).getValue();

        // only int and float are supported
        if (value instanceof Integer)
            stack.pushInt((Integer) value);
        else if (value instanceof Float)
            stack.pushFloat((Float) value);
    }

}
