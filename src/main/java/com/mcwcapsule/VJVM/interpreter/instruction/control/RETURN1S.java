package com.mcwcapsule.VJVM.interpreter.instruction.control;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;
import lombok.var;

import static com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors.*;

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
