package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VJVM;
import lombok.val;

public class NEWARRAY extends Instruction {
    private static final String[] arrType = {
        null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
    };

    @Override
    public void fetchAndRun(JThread thread) {
        val atype = thread.getPC().getUnsignedByte();
        assert atype >= 4;
        JClass jClass;
        try {
            jClass = VJVM.getBootstrapLoader().loadClass(arrType[atype]);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (jClass.getInitState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        val stack = thread.getCurrentFrame().getOpStack();
        val ref = ArrayUtil.newInstance(jClass, stack.popInt());
        stack.pushAddress(ref);
    }

}
