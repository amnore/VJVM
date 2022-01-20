package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;

public class AIFASTORE extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var value = stack.popInt();
        var index = stack.popInt();
        var obj = stack.popAddress();
        var heap = VJVM.heap();
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(obj - 1));
        assert jClass.array();
        // the address of the element is obj + sizeof(ArrayClass) + index
        slots.int_(obj + jClass.instanceSize() + index, value);
    }
}
