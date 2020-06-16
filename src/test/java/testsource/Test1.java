package testsource;

public class Test1 extends RuntimeException implements AutoCloseable, Comparable<Test1> {
    int a = 1;
    public int b = 2;
    protected static String c = "3";
    private final int d = 4;
    static {
        c = "6";
    }

    Test1() {
        System.out.println("7");
    }

    int foo(int e) {
        return -e;
    }

    @Override
    public void close() {
    }

    @Override
    public int compareTo(Test1 o) {
        return 0;
    }

}
