package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;
import vjvm.vm.VJVM;
import lombok.val;

public class GETFIELD extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val obj = stack.popAddress();
        val ref = (FieldRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());

        // log
        System.err.println(ref.getName());

        if (obj == 0)
            throw new NullPointerException();
        FieldInfo field;
        try {
            ref.resolve(frame.getJClass());
            field = ref.getInfo();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val slots = VJVM.getHeap().getSlots();
        if (field.getSize() == 2)
            stack.pushLong(slots.getLong(obj + field.getOffset()));
        else stack.pushInt(slots.getInt(obj + field.getOffset()));
    }

}
