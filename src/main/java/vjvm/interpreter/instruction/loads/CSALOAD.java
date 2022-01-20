package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.vm.VJVM;
import lombok.val;

public class CSALOAD extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val index = stack.popInt();
        val obj = stack.popAddress();
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(obj - 1));
        assert jClass.isArray();
        val raw = slots.getRaw();
        // the address of the element (at raw buffer) is obj * 4 + sizeof(ArrayClass) * 4 + index * 2
        val value = raw.getShort((obj + jClass.getInstanceSize()) * 4 + index * 2);
        stack.pushInt(value);
    }
}
