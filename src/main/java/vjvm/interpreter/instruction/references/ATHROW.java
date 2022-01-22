package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VMContext;
import lombok.val;

public class ATHROW extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var obj = thread.currentFrame().opStack().popAddress();

        // if the reference is null, throw an NullPointerException instead
        if (obj == 0) {
            var nptrClass = thread.context().bootstrapLoader().loadClass("java/lang/NullPointerException");
            obj = nptrClass.createInstance();
        }

        thread.throwException(obj);
    }
}
