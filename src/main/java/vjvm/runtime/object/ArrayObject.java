package vjvm.runtime.object;

import lombok.Getter;
import vjvm.classfiledefs.Descriptors;
import vjvm.runtime.JClass;

import java.nio.ByteBuffer;

public class ArrayObject extends JObject {
    private final ByteBuffer elements;
    @Getter
    private final int length;

    public ArrayObject(JClass jClass, int length) {
        super(jClass);

        var elemSize = Descriptors.size(type().name().substring(1));
        elements = ByteBuffer.allocate(elemSize * length);
        this.length = length;
        data().address(jClass.findField("length", "I").offset(), length);
    }

    public ArrayObject(JClass jClass, byte[] data) {
        this(jClass, data.length);
        elements.put(0, data);
    }

    public byte[] value() {
        return elements.array();
    }

    public int int_(int index) {
        return elements.getInt(index * 4);
    }

    public void int_(int index, int value) {
        elements.putInt(index*4, value);
    }

    public long long_(int index) {
        return elements.getLong(index * 8);
    }

    public void long_(int index, long value) {
        elements.putLong(index * 8, value);
    }

    public float float_(int index) {
        return elements.getFloat(index * 4);
    }

    public void float_(int index, float value) {
        elements.putFloat(index * 4, value);
    }

    public double double_(int index) {
        return elements.getDouble(index * 8);
    }

    public void double_(int index, double value) {
        elements.putDouble(index * 8, value);
    }

    public int address(int index) {
        return elements.getInt(index * 4);
    }

    public void address(int index, int value) {
        elements.putInt(index * 4, value);
    }

    public byte byte_(int index) {
        return elements.get(index);
    }

    public void byte_(int index, byte value) {
        elements.put(index, value);
    }

    public short short_(int index) {
        return elements.getShort(index * 2);
    }

    public void short_(int index, short value) {
        elements.putShort(index * 2, value);
    }

    public char char_(int index) {
        return elements.getChar(index * 2);
    }

    public void char_(int index, char value) {
        elements.putChar(index * 2, value);
    }
}
