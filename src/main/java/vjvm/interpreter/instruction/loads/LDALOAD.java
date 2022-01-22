package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class LDALOAD extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var index = stack.popInt();
        var obj = stack.popAddress();
        var heap = VMContext.heap();
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(obj - 1));
        assert jClass.array();
        // the address of the element is obj + sizeof(ArrayClass) + index * 2
        var value = slots.long_(obj + jClass.instanceSize() + index * 2);
        stack.pushLong(value);
    }
}
