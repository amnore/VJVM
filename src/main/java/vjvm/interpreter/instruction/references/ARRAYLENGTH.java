package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;

public class ARRAYLENGTH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var ref = stack.popAddress();
        var heap = VJVM.heap();
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(ref - 1));
        var len = slots.int_(ref + jClass.instanceSize() - 1);
        stack.pushInt(len);
    }

}
