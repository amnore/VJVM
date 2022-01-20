package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.ArrayUtil;
import lombok.val;

public class ANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val count = stack.popInt();
        val ref = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        JClass arrayClass;
        try {
            ref.resolve(frame.getJClass());
            arrayClass = ref.getJClass().getClassLoader().loadClass("[L" + ref.getName() + ';');
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (arrayClass.getInstanceSize() != JClass.InitState.INITIALIZED)
            arrayClass.tryInitialize(thread);
        val arr = ArrayUtil.newInstance(arrayClass, count);
        stack.pushAddress(arr);
    }

}
