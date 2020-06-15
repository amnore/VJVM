package com.mcwcapsule.VJVM;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteOrder;

import com.mcwcapsule.VJVM.runtime.Slots;

import org.junit.jupiter.api.Test;

import lombok.var;

public class SlotsTest {
    @Test
    void testInt() {
        var slots = new Slots();
        slots.pushInt(0x12345678);
        slots.pushInt(0xfedcba98);
        assertEquals(0x12345678, slots.getInt(0));
        assertEquals(0xfedcba98, slots.popInt());
        assertEquals(0x12345678, slots.popInt());
    }

    @Test
    void testShort() {
        var slots = new Slots();
        slots.pushShort((short) 0x1234);
        slots.pushShort((short) 0xfedc);
        assertEquals((short) 0x1234, slots.getInt(0));
        assertEquals((short) 0xfedc, slots.popInt());
        assertEquals((short) 0x1234, slots.popInt());
    }

    @Test
    void testByte() {
        var slots = new Slots();
        slots.pushByte((byte) 0x12);
        slots.pushByte((byte) 0xfe);
        assertEquals((byte) 0x12, slots.getInt(0));
        assertEquals((byte) 0xfe, slots.popInt());
        assertEquals((byte) 0x12, slots.popInt());
    }

    @Test
    void testFloat() {
        var slots = new Slots();
        slots.pushFloat(1234f);
        assertEquals(1234f, slots.popFloat());
    }

    @Test
    void testLong() {
        var slots = new Slots();
        slots.pushLong(0x1234567890abcdefL);
        assertEquals(0x1234567890abcdefL, slots.popLong());
    }

    @Test
    void testDouble() {
        var slots = new Slots();
        slots.pushDouble(1.23e-50);
        assertEquals(1.23e-50, slots.popDouble());
    }

    @Test
    void testByteOrder() {
        var slots = new Slots();
        slots.pushInt(0x1234);
        slots.pushInt(0xfedc);
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN))
            assertEquals(0x12340000fedcL, slots.popLong());
        else
            assertEquals(0xfedc00001234L, slots.popLong());
    }

    @Test
    void testOverflow() {
        var slots = new Slots();
        slots.pushInt(0);
        assertThrows(AssertionError.class, () -> slots.getLong(0));
        assertThrows(AssertionError.class, () -> slots.getInt(1));
    }

    @Test
    void testUnderflow() {
        var slots = new Slots();
        assertThrows(AssertionError.class, () -> slots.popInt());
        slots.pushInt(0);
        assertThrows(AssertionError.class, () -> slots.popLong());
    }
}
