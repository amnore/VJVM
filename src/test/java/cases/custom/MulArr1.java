package cases.custom;

import cases.TestUtil;

public class MulArr1 {
    public static void main(String[] args) {
        int[][] arr = new int[2][3];
        for (int i = 0; i < 6; ++i)
            arr[i / 3][i % 3] = i;
        int r = 0;
        for (int[] ints : arr)
            for (int anInt : ints) r += anInt;
        TestUtil.equalInt(r, 15);
    }
}
