package vjvm.runtime;

import lombok.Getter;
import vjvm.vm.VMContext;

@Getter
public class OperandStack {
    private final Slots slots;
    private int top;

    public OperandStack(int stackSize) {
        slots = new Slots(stackSize);
        top = 0;
    }

    public void pushInt(int value) {
        slots.int_(top++, value);
    }

    public int popInt() {
        return slots.int_(--top);
    }

    public void pushFloat(float value) {
        slots.float_(top++, value);
    }

    public float popFloat() {
        return slots.float_(--top);
    }

    public void pushLong(long value) {
        slots.long_(top, value);
        top += 2;
    }

    public long popLong() {
        top -= 2;
        return slots.long_(top);
    }

    public void pushDouble(double value) {
        slots.double_(top, value);
        top += 2;
    }

    public double popDouble() {
        top -= 2;
        return slots.double_(top);
    }

    public void pushAddress(int value) {
        pushInt(value);
    }

    public int popAddress() {
        return popInt();
    }

    public void popSlots(int count) {
        assert top >= count;
        top -= count;
    }

    public void clear() {
        top = 0;
    }

    @Override
    public String toString() {
        return "OperandStack{" + "slots=" + slots +
            ", top=" + top +
            '}';
    }
}
