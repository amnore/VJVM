package cases.custom;

import cases.TestUtil;

public class Str1 {
    public static void main(String[] args) {
        String s = "yggtqlyydsddw";
        TestUtil.equalInt(s.startsWith("ygg") ? 1 : 0, 1);
        TestUtil.equalInt(s.endsWith("ddw") ? 1 : 0, 1);
        TestUtil.equalInt(s.indexOf("yyds"), 6);
        TestUtil.equalInt(s.lastIndexOf("s"), 9);
        TestUtil.equalInt(s.replace('y', 'w').equals("wggtqlwwdsddw") ? 1 : 0, 1);
        StringBuilder sb = new StringBuilder("wgg");
        TestUtil.equalInt(sb.toString().equals("wgg") ? 1 : 0, 1);
        TestUtil.reach(1);
        sb.append(s.substring(3));
        TestUtil.equalInt(sb.toString().equals("wggtqlyydsddw") ? 1 : 0, 1);
    }
}
