package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VJVM;

public class NEWARRAY extends Instruction {
    private static final String[] arrType = {
        null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
    };

    @Override
    public void fetchAndRun(JThread thread) {
        var atype = thread.pc().ubyte();
        assert atype >= 4;
        JClass jClass;
        try {
            jClass = VJVM.bootstrapLoader().loadClass(arrType[atype]);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (jClass.initState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        var stack = thread.currentFrame().opStack();
        var ref = ArrayUtil.newInstance(jClass, stack.popInt());
        stack.pushAddress(ref);
    }

}
