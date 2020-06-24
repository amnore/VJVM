package testsource;

import static testsource.TestUtil.assertEquals;

public class Test6 {
    public static void main(String[] args) {
        Son son = new Son();
        son.testson();
        son.testparent();
    }
}

class Parent {
    int foo() {
        return 1;
    }

    protected float bar() {
        return 2;
    }

    private long cal() {
        return 3;
    }

    public double dic() {
        return 4;
    }

    public void testparent() {
        assertEquals(cal(), 3);
    }
}

class Son extends Parent {
    int foo() {
        return 5;
    }

    protected float bar() {
        return 6;
    }

    private long cal() {
        return 7;
    }

    public double dic() {
        return 8;
    }

    public void testson() {
        assertEquals(foo(), 5);
        assertEquals(bar(), 6);
        assertEquals(cal(), 7);
        assertEquals(dic(), 8);
    }
}
