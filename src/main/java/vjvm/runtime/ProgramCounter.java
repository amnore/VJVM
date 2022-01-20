package vjvm.runtime;

import java.nio.ByteBuffer;

public class ProgramCounter {
    private final ByteBuffer buf;

    private int position;

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

    public void position(int pos) {
        this.position = pos;
        buf.position(pos);
    }

    public int position() {
        return position;
    }

    public void updatePC() {
        position = buf.position();
    }
}
