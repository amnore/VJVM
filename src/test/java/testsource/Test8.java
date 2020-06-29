package testsource;

import static testsource.TestUtil.assertEquals;

public class Test8 {
    public static void main(String[] args) {
        String s = "yggtqlyydsddw";
        assertEquals(s.startsWith("ygg"), true);
        assertEquals(s.endsWith("ddw"), true);
        assertEquals(s.length(), 13);
    }
}
