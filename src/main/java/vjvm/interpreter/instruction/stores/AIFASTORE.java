package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;
import lombok.val;

public class AIFASTORE extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popInt();
        val index = stack.popInt();
        val obj = stack.popAddress();
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(obj - 1));
        assert jClass.isArray();
        // the address of the element is obj + sizeof(ArrayClass) + index
        slots.setInt(obj + jClass.getInstanceSize() + index, value);
    }
}
