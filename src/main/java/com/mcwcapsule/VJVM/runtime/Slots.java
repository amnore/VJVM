package com.mcwcapsule.VJVM.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import lombok.val;
import lombok.var;

/**
 * Slots represents an growable array of JVM slots as defined in the specification.
 * It supports getting and putting primitive data types, including address.
 * the data are stored in native endian.
 */
public class Slots {
    private ByteBuffer buf;

    public Slots() {
        this(16);
    }

    public Slots(int slotSize) {
        buf = ByteBuffer.allocate(slotSize * 4);
        buf.order(ByteOrder.nativeOrder());
    }

    public byte getByte(int index) {
        return (byte) getInt(index);
    }

    public void setByte(int index, byte value) {
        setInt(index, value);
    }

    public short getShort(int index) {
        return (short) getInt(index);
    }

    public void setShort(int index, short value) {
        setInt(index, value);
    }

    public int getInt(int index) {
        assert isInbound(index);
        return buf.getInt(index * 4);
    }

    public void setInt(int index, int value) {
        assert isInbound(index);
        buf.putInt(index * 4, value);
    }

    public long getLong(int index) {
        assert isInbound(index + 1);
        return buf.getLong(index * 4);
    }

    public void setLong(int index, long value) {
        assert isInbound(index + 1);
        buf.putLong(index * 4, value);
    }

    public char getChar(int index) {
        return (char) getInt(index);
    }

    public void setChar(int index, char value) {
        setInt(index, value);
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    public void setFloat(int index, float value) {
        setInt(index, Float.floatToRawIntBits(value));
    }

    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public void setDouble(int index, double value) {
        setLong(index, Double.doubleToRawLongBits(value));
    }

    public boolean getBool(int index) {
        return getInt(index) != 0;
    }

    public void setBool(int index, boolean value) {
        setInt(index, value ? 1 : 0);
    }

    public int getAddress(int index) {
        return getInt(index);
    }

    public void setAddress(int index, int value) {
        setInt(index, value);
    }

    public void pushByte(byte value) {
        pushInt(value);
    }

    public void pushShort(short value) {
        pushInt(value);
    }

    public void pushInt(int value) {
        if (buf.position() == buf.limit())
            grow();
        buf.putInt(value);
    }

    public void pushLong(long value) {
        if (buf.position() >= buf.limit() - 1)
            grow();
        buf.putLong(value);
    }

    public void pushChar(char value) {
        pushInt(value);
    }

    public void pushFloat(float value) {
        pushInt(Float.floatToRawIntBits(value));
    }

    public void pushDouble(double value) {
        pushLong(Double.doubleToRawLongBits(value));
    }

    public void pushBool(boolean value) {
        pushInt(value ? 1 : 0);
    }

    public void pushAddress(int value) {
        pushInt(value);
    }

    public byte popByte() {
        return (byte) popInt();
    }

    public short popShort() {
        return (short) popInt();
    }

    public int popInt() {
        assert buf.position() >= 4;
        buf.position(buf.position() - 4);
        return buf.getInt(buf.position());
    }

    public long popLong() {
        assert buf.position() >= 8;
        buf.position(buf.position() - 8);
        return buf.getLong(buf.position());
    }

    public char popChar() {
        return (char) popInt();
    }

    public float popFloat() {
        return Float.intBitsToFloat(popInt());
    }

    public double popDouble() {
        return Double.longBitsToDouble(popLong());
    }

    public boolean popBoolean() {
        return popInt() != 0;
    }

    public int popAddress() {
        return popInt();
    }

    private void grow() {
        var newBuf = ByteBuffer.allocate(buf.limit() * 2);
        newBuf.order(ByteOrder.nativeOrder());
        newBuf.put(buf.array());
        buf = newBuf;
    }

    private boolean isInbound(int index) {
        return index * 4 + 4 <= buf.position();
    }
}
