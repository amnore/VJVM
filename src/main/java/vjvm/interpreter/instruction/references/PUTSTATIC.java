package vjvm.interpreter.instruction.references;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTSTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var fieldRef = (FieldRef) frame.dynLink().constant(thread.pc().ushort());

        var jClass = fieldRef.jClass();
        if (jClass.initState() != JClass.InitState.INITIALIZED) {
            jClass.initialize(thread);
            assert jClass.initState() == JClass.InitState.INITIALIZED;
        }
        var slots = jClass.staticFields();
        var field = fieldRef.info();
        var stack = frame.opStack();
        if (fieldRef.size() == 2)
            slots.long_(field.offset(), stack.popLong());
        else {
            var value = stack.popInt();
            if (fieldRef.descriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            slots.int_(field.offset(), value);
        }
    }

}
