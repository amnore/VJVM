package cases.custom;

import static cases.TestUtil.equalInt;

public class Obj5 {
    public static void main(String[] args) {
        Obj5 a = new Obj6();
        equalInt(a.foo(), 2);
        equalInt(a.bar(), 3);
        equalInt(a.cii(), 4);
    }

    int foo() {
        return 1;
    }

    int bar() {
        return 3;
    }

    private int cii() {
        return 4;
    }

    public static class Obj6 extends Obj5 {
        @Override
        int foo() {
            return 2;
        }
    }
}

