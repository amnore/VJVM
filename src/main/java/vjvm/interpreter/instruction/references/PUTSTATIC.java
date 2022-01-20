package vjvm.interpreter.instruction.references;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;
import lombok.val;

public class PUTSTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val fieldRef = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            fieldRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = fieldRef.getJClass();
        if (jClass.getInitState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        val slots = jClass.getStaticFields();
        val field = fieldRef.getInfo();
        val stack = frame.getOpStack();
        if (fieldRef.getSize() == 2)
            slots.setLong(field.getOffset(), stack.popLong());
        else {
            var value = stack.popInt();
            if (fieldRef.getDescriptor().charAt(0) == FieldDescriptors.DESC_boolean)
                value &= 1;
            slots.setInt(field.getOffset(), value);
        }
    }

}
