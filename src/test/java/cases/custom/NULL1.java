package cases.custom;

import cases.TestUtil;

public class NULL1 {
    public static void main(String[] args) {
        NULL1 n = null;
        if (n == null)
            TestUtil.reach(1);
        else
            TestUtil.reach(2);
        n = new NULL1();
        if (n == null)
            TestUtil.reach(3);
        else TestUtil.reach(4);
    }
}
