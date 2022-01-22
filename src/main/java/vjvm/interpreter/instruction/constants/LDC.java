package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.classdata.constant.ValueConstant;

public class LDC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var index = thread.pc().ubyte();
        var constant = frame.dynLink().constant(index);
        if (constant instanceof ClassRef) {
            stack.pushAddress(((ClassRef) constant).jClass().classObject());
        } else {
            var value = ((ValueConstant) frame.dynLink().constant(index)).value();
            if (value instanceof Integer)
                stack.pushInt((Integer) value);
            else if (value instanceof Float)
                stack.pushFloat((Float) value);
        }
    }

}
