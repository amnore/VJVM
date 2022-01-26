package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.classdata.constant.ValueConstant;

public class LDC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var stack = frame.stack();
        var index = thread.pc().ubyte();
        var constant = frame.link().constant(index);
        if (constant instanceof ClassRef) {
            stack.pushAddress(((ClassRef) constant).jClass().classObject().address());
        } else {
            var value = ((ValueConstant) frame.link().constant(index)).value(thread.context());
            if (value instanceof Integer)
                stack.pushInt((Integer) value);
            else if (value instanceof Float)
                stack.pushFloat((Float) value);
        }
    }

}
