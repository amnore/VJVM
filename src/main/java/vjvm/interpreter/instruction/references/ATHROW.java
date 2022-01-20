package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;
import lombok.val;

public class ATHROW extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var obj = thread.currentFrame().opStack().popAddress();

        // if the reference is null, throw an NullPointerException instead
        if (obj == 0) {
            try {
                var nptrClass = VJVM.bootstrapLoader().loadClass("java/lang/NullPointerException");
                obj = nptrClass.createInstance();
            } catch (ClassNotFoundException e) {
                throw new Error(e);
            }
        }

        thread.throwException(obj);
    }
}
