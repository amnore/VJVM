package com.mcwcapsule.VJVM.runtime;

import java.nio.ByteBuffer;

public class ProgramCounter {
    private final ByteBuffer buf;

    public ProgramCounter(byte[] code) {
        buf = ByteBuffer.wrap(code);
    }

    public byte getByte() {
        return buf.get();
    }

    public int getUnsignedByte() {
        return Byte.toUnsignedInt(buf.get());
    }

    public short getShort() {
        return buf.getShort();
    }

    public int getUnsignedShort() {
        return Short.toUnsignedInt(buf.getShort());
    }

    public void move(int offset) {
        buf.position(buf.position() + offset);
    }
}
