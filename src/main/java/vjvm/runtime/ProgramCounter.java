package vjvm.runtime;

import java.nio.ByteBuffer;

public class ProgramCounter {
    private final ByteBuffer buf;

    public ProgramCounter(byte[] code) {
        buf = ByteBuffer.wrap(code);
    }

    public byte byte_() {
        return buf.get();
    }

    public int ubyte() {
        return Byte.toUnsignedInt(buf.get());
    }

    public short short_() {
        return buf.getShort();
    }

    public int ushort() {
        return Short.toUnsignedInt(buf.getShort());
    }

    public void move(int offset) {
        buf.position(buf.position() + offset);
    }

    public void position(int pos) {
        buf.position(pos);
    }

    public int position() {
        return buf.position();
    }
}
