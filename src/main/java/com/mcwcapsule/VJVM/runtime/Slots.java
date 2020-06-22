package com.mcwcapsule.VJVM.runtime;

import java.nio.ByteBuffer;

/**
 * Slots represents an growable array of JVM slots as defined in the specification.
 * It supports getting and putting primitive data types, including address.
 */
public class Slots {
    private final ByteBuffer buf;

    public Slots(int slotSize) {
        buf = ByteBuffer.allocate(slotSize * 4);
    }

    public int getInt(int index) {
        return buf.getInt(index * 4);
    }

    public void setInt(int index, int value) {
        buf.putInt(index * 4, value);
    }

    public long getLong(int index) {
        return buf.getLong(index * 4);
    }

    public void setLong(int index, long value) {
        buf.putLong(index * 4, value);
    }

    public float getFloat(int index) {
        return buf.getFloat(index * 4);
    }

    public void setFloat(int index, float value) {
        buf.putFloat(index * 4, value);
    }

    public double getDouble(int index) {
        return buf.getDouble(index * 4);
    }

    public void setDouble(int index, double value) {
        buf.putDouble(index * 4, value);
    }

    public int getAddress(int index) {
        return getInt(index);
    }

    public void setAddress(int index, int value) {
        setInt(index, value);
    }

    public int size() {
        return buf.position() / 4;
    }
}
