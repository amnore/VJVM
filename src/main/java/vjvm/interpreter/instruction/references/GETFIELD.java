package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;
import vjvm.vm.VJVM;

public class GETFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var obj = stack.popAddress();
        var ref = (FieldRef) frame.dynLink().constant(thread.pc().ushort());

        // log
        System.err.println(ref.name());

        if (obj == 0)
            throw new NullPointerException();
        FieldInfo field;
        try {
            ref.resolve(frame.jClass());
            field = ref.info();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        var slots = VJVM.heap().slots();
        if (field.size() == 2)
            stack.pushLong(slots.long_(obj + field.offset()));
        else stack.pushInt(slots.int_(obj + field.offset()));
    }

}
