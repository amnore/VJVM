package cases.custom;

import static cases.TestUtil.equalInt;

public class Obj6 {
    static final int a = 1;
    static int b = 2;
    int c = 3;

    public static void main(String[] args) {
        equalInt(a, 1);
        equalInt(b, 2);
        Obj6 d = new Obj6();
        equalInt(d.c, 3);
        d.c = 4;
        equalInt(d.c, 4);
        b = 5;
        equalInt(b, 5);
    }
}
