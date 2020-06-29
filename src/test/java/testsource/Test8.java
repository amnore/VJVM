package testsource;

import static testsource.TestUtil.assertEquals;

public class Test8 {
    public static void main(String[] args) {
        String s = "yggtqlyydsddw";
        assertEquals(s.startsWith("ygg"), true);
        assertEquals(s.endsWith("ddw"), true);
        assertEquals(s.replace("ygg", "wgg").equals("wggtqlyydsddw"), true);
        assertEquals(s.length(), 13);
        assertEquals(s.lastIndexOf('y'), 7);
    }
}
