package vjvm.interpreter.instruction.references;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;
import vjvm.vm.VMContext;

public class PUTFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var fieldRef = (FieldRef) frame.dynLink().constant(thread.pc().ushort());

        // log
        System.err.println(fieldRef.name());

        var stack = frame.opStack();
        var field = fieldRef.info();
        var slots = thread.context().heap().slots();
        if (fieldRef.size() == 1) {
            var value = stack.popInt();
            var ref = stack.popAddress();
            if (field.descriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            slots.int_(ref + field.offset(), value);
        } else {
            var value = stack.popLong();
            var ref = stack.popAddress();
            slots.long_(ref + field.offset(), value);
        }
    }

}
