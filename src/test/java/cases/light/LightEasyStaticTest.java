package cases.light;


import cases.TestUtil;

/**
 * Description:
 *
 * @author xxz
 * Created on 07/01/2020
 */
public class LightEasyStaticTest {
    private static final int a = 7;

    static {
        TestUtil.reach(3);
    }

    public static void main(String[] args) {
        TestUtil.reach(4);
        TestUtil.equalInt(7, a);
    }
}
