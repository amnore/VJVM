package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VMContext;

public class AIFALOAD extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var index = stack.popInt();
        var obj = stack.popAddress();
        var heap = thread.context().heap();
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(obj - 1));
        assert jClass.array();
        // the address of the element is obj + sizeof(ArrayClass) + index
        var value = slots.int_(obj + jClass.instanceSize() + index);
        stack.pushInt(value);
    }
}
