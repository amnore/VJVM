package vjvm.interpreter.instruction.references;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var fieldRef = (FieldRef) frame.dynLink().constant(thread.pc().ushort());

        // log
        System.err.println(fieldRef.name());

        var stack = frame.opStack();
        var field = fieldRef.info();
        var heap = thread.context().heap();
        if (fieldRef.size() == 1) {
            var value = stack.popInt();
            var obj = heap.get(stack.popAddress());

            if (field.descriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            obj.data().int_(field.offset(), value);
        } else {
            var value = stack.popLong();
            var obj = heap.get(stack.popAddress());
            obj.data().long_(field.offset(), value);
        }
    }

}
