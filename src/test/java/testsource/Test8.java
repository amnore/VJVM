package testsource;

import static testsource.TestUtil.assertEquals;

public class Test8 {
    public static void main(String[] args) {
        String[] _1 = new String[]{"3", "3.1", "3.14"};
        String _2 = "yggtqlyydsddw";
        StringBuilder bu = new StringBuilder();
        bu.append("ygg");
        bu.append("tql");
        bu.append("yyds");
        bu.append("ddw");
        assertEquals(Double.parseDouble(_1[0]), 3, 1e-6);
        assertEquals(Double.parseDouble(_1[1]), 3.1, 1e-6);
        assertEquals(Double.parseDouble(_1[2]), 3.14, 1e-6);
        assertEquals(_2.equals(bu.toString()), true);
        assertEquals(_2 == bu.toString(), false);
        assertEquals(_2.intern() == bu.toString().intern(), true);
        assertEquals(_2.startsWith("ygg"), true);
        assertEquals(_2.endsWith("ddw"), true);
        assertEquals(_2.charAt(3), 't');
        assertEquals(_2.lastIndexOf("y"), 7);
        assertEquals(_2.length(), 13);
        assertEquals(_2.replace("ygg", "tscdl").equals("tscdltqlyydsddw"), true);
        assertEquals(_2.startsWith("ygg"), true);
    }
}
