package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;
import vjvm.vm.VMContext;

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
        ref.resolve(frame.jClass());
        field = ref.info();

        var slots = VMContext.heap().slots();
        if (field.size() == 2)
            stack.pushLong(slots.long_(obj + field.offset()));
        else stack.pushInt(slots.int_(obj + field.offset()));
    }

}
