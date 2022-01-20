package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;

public class CSASTORE extends Instruction {
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
        // the address of the element (at raw array) is obj * 4 + sizeof(ArrayClass) * 4 + index * 2
        slots.shortAt((obj + jClass.instanceSize()) * 4 + index * 2, (short) value);
    }
}
