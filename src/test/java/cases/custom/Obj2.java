package cases.custom;

import cases.TestUtil;

public class Obj2 {
    public static void main(String[] args) {
        CObj2 a = new CObj2();
        if (a instanceof Obj2)
            TestUtil.reach(1);
        else TestUtil.reach(2);
        Obj2 b = a.mk();
        if (b instanceof CObj2)
            TestUtil.reach(3);
        else TestUtil.reach(4);
    }

    public static class CObj2 extends Obj2 {
        public CCObj2 mk() {
            return new CCObj2();
        }

        public class CCObj2 extends CObj2 {
        }
    }
}
