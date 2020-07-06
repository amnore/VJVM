package cases.custom;

import cases.TestUtil;

public class Exc1 {
    static boolean a = true;

    public static void main(String[] args) {
        try {
            thr();
        } catch (RuntimeException e) {
            TestUtil.reach(1);
        }
        try {
            thr2();
        } catch (Error e) {
            TestUtil.reach(2);
        } catch (ExceptionA e) {
            TestUtil.reach(3);
        } catch (RuntimeException e) {
            TestUtil.reach(4);
        } finally {
            TestUtil.reach(5);
        }
        try {
            thr3();
        } catch (RuntimeException e) {
            TestUtil.reach(10);
        }
        TestUtil.reach(11);
        try {
            throw new Error();
        } finally {
            TestUtil.reach(6);
        }
    }

    static void thr() {
        if (a)
            throw new RuntimeException();
        TestUtil.reach(7);
    }

    static void thr2() {
        try {
            throw new ExceptionA();
        } catch (RuntimeException e) {
            TestUtil.reach(8);
            throw e;
        }
    }

    static void thr3() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            TestUtil.reach(9);
        }
    }

    static class ExceptionA extends RuntimeException {
    }
}
