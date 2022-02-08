package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.JObject;

public class ATHROW extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var ctx = thread.context();
        var addr = thread.top().stack().popAddress();

        thread.exception(addr == 0 ? ctx.heap().get(addr):
            new JObject(ctx.bootstrapLoader().loadClass("java/lang/NullPointerException")));
    }
}
