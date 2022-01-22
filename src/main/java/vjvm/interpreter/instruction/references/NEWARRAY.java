package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VMContext;

public class NEWARRAY extends Instruction {
    private static final String[] arrType = {
        null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
    };

    @Override
    public void fetchAndRun(JThread thread) {
        var atype = thread.pc().ubyte();
        assert atype >= 4;
        JClass jClass = thread.context().bootstrapLoader().loadClass(arrType[atype]);

        if (jClass.initState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        var stack = thread.currentFrame().opStack();
        var ref = ArrayUtil.newInstance(jClass, stack.popInt(), thread.context().heap());
        stack.pushAddress(ref);
    }

}
