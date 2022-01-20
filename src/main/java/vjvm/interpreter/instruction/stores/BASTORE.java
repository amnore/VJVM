package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;
import lombok.val;

public class BASTORE extends Instruction {
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
        val raw = slots.getRaw();
        // the address of the element (at raw array) is obj * 4 + sizeof(ArrayClass) * 4 + index
        raw.put((obj + jClass.getInstanceSize()) * 4 + index, (byte) value);
    }
}
