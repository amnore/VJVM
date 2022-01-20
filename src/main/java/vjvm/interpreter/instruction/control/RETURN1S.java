package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

import static vjvm.classfiledefs.FieldDescriptors.*;

public class RETURN1S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var ret = thread.getCurrentFrame().getOpStack().popInt();
        val returnType = thread.getCurrentFrame().getMethodInfo().getDescriptor().charAt(0);
        switch (returnType) {
            case DESC_boolean:
                ret &= 1;
                break;
            case DESC_byte:
                ret = (byte) ret;
                break;
            case DESC_char:
                ret = (char) ret;
                break;
            case DESC_short:
                ret = (short) ret;
                break;
        }
        thread.popFrame();
        thread.getCurrentFrame().getOpStack().pushInt(ret);
    }

}
