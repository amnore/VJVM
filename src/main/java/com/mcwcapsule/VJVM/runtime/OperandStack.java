package com.mcwcapsule.VJVM.runtime;

import lombok.Getter;

@Getter
public class OperandStack {
    private final Slots slots;
    private int top;

    public OperandStack(int stackSize) {
        slots = new Slots(stackSize);
        top = 0;
    }

    public void pushInt(int value) {
        slots.setInt(top++, value);
    }

    public int popInt() {
        return slots.getInt(--top);
    }

    public void pushFloat(float value) {
        slots.setFloat(top++, value);
    }

    public float popFloat() {
        return slots.getFloat(--top);
    }

    public void pushLong(long value) {
        slots.setLong(top, value);
        top += 2;
    }

    public long popLong() {
        top -= 2;
        return slots.getLong(top);
    }

    public void pushDouble(double value) {
        slots.setDouble(top, value);
        top += 2;
    }

    public double popDouble() {
        top -= 2;
        return slots.getDouble(top);
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
}
