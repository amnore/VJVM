package testsource;

import static testsource.TestUtil.assertEquals;

public class Test5 {
    public static void main(String[] args) {
        int a = 1;
        long b = 2;
        float c = 3;
        double d = 4;
        assertEquals(a + 1, b);
        assertEquals(c, d - 1);
        assertEquals(c, (float) (d - 1));
        assertEquals(true, a * b * c * d == d * c * b / a);
        int e = (short) 0xdfff;
        assertEquals(e, 0xffffdfff);
        int f = 129837;
        assertEquals(e ^ f, -121646);
        assertEquals(e | f, -1);
        assertEquals((long) e & f, 0x1db2d);
    }
}
