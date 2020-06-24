package testsource;

import static testsource.TestUtil.assertEquals;

public class Test7 {
    public static void main(String[] args) {
        int[] _1 = new int[]{1, 2, 3, 4};
        long[] _2 = new long[]{5, 6, 7, 8};
        float[] _3 = new float[]{9, 10, 11, 12};
        double[] _4 = new double[]{13, 14, 15, 16};
        boolean[] _5 = new boolean[]{true, false, true, false};
        short[] _6 = new short[]{-17, -18, -19, -20};
        char[] _7 = new char[]{17, 18, 19, 20};
        byte[] _8 = new byte[]{-21, -22, -23, -24};
        assertEquals(_1[0] << _1[1], _1[3]);
        assertEquals(_2[3] - _2[0], _1[2]);
        assertEquals(_4[1] * _3[3], _2[2] * _2[3] * _1[3]);
        assertEquals(_4[0] - _2[3] - _2[0] == 0, _5[0]);
        assertEquals(~_6[1], _7[0]);
        assertEquals(_6[3] - 1, _8[0]);
        assertEquals(_8[2] * _8[3], 40);
    }
}
