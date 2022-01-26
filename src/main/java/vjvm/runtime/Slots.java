package vjvm.runtime;

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

    public int int_(int index) {
        return buf.getInt(index * 4);
    }

    public void int_(int index, int value) {
        buf.putInt(index * 4, value);
    }

    public long long_(int index) {
        return buf.getLong(index * 4);
    }

    public void long_(int index, long value) {
        buf.putLong(index * 4, value);
    }

    public float float_(int index) {
        return buf.getFloat(index * 4);
    }

    public void float_(int index, float value) {
        buf.putFloat(index * 4, value);
    }

    public double double_(int index) {
        return buf.getDouble(index * 4);
    }

    public void double_(int index, double value) {
        buf.putDouble(index * 4, value);
    }

    public int address(int index) {
        return buf.getInt(index * 4);
    }

    public void address(int index, int value) {
        buf.putInt(index, value);
    }

    public int size() {
        return buf.limit() / 4;
    }

    public void copyTo(int begin, int length, Slots dest, int destBegin) {
        if (dest == this && destBegin > begin)
            for (int i = length - 1; i >= 0; --i)
                dest.int_(destBegin + i, int_(begin + i));
        else
            for (int i = 0; i < length; ++i) {
                dest.int_(destBegin + i, int_(begin + i));
            }
    }

    @Override
    public String toString() {
        StringBuilder bu = new StringBuilder("[");
        for (int i = 0; i < size(); ++i) {
            bu.append(int_(i));
            if (i != size() - 1)
                bu.append(", ");
        }
        bu.append(']');
        return bu.toString();
    }
}
