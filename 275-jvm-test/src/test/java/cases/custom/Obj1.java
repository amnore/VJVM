package cases.custom;

import cases.TestUtil;

public class Obj1 {
    int v = 1;

    public static void main(String[] args) {
        Obj1 a = new Obj1();
        TestUtil.equalInt(a.v, 1);
        Obj1 b = null;
        if (b instanceof Obj1)
            TestUtil.reach(2);
        else TestUtil.reach(3);
        if (a instanceof Obj1)
            TestUtil.reach(4);
        else TestUtil.reach(5);
        a.v = 6;
        TestUtil.reach(a.v);
    }
}
