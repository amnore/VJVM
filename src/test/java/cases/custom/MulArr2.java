package cases.custom;

import cases.TestUtil;

public class MulArr2 {
    public static void main(String[] args) {
        T[][] arr = new T[2][3];
        for (int i = 0; i < 6; ++i)
            arr[i / 3][i % 3] = new T(i);
        int r = 0;
        for (T[] ints : arr)
            for (T anInt : ints) r += anInt.v;
        TestUtil.equalInt(r, 15);
    }
}

class T {
    int v;

    T(int n) {
        v = n;
    }
}
