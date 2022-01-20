package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.ArrayUtil;

public class ANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        var count = stack.popInt();
        var ref = (ClassRef) frame.dynLink().constant(thread.pc().ushort());
        JClass arrayClass;
        try {
            ref.resolve(frame.jClass());
            arrayClass = ref.jClass().classLoader().loadClass("[L" + ref.name() + ';');
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (arrayClass.instanceSize() != JClass.InitState.INITIALIZED)
            arrayClass.tryInitialize(thread);
        var arr = ArrayUtil.newInstance(arrayClass, count);
        stack.pushAddress(arr);
    }

}
