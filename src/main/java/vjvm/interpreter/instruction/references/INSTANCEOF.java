package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.vm.VJVM;
import lombok.val;

public class INSTANCEOF extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val classRef = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        val obj = stack.popAddress();
        if (obj == 0) {
            stack.pushInt(0);
            return;
        }
        try {
            classRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = classRef.getJClass();
        val objClass = VJVM.getHeap().getJClass(VJVM.getHeap().getSlots().getInt(obj - 1));
        System.err.println(jClass.getThisClass().getName());
        System.err.println(objClass.getThisClass().getName());
        stack.pushInt(objClass.canCastTo(jClass) ? 1 : 0);
    }

}
