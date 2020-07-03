package cases.light;


import cases.TestUtil;

/**
 * Description:
 *
 * @author xxz
 * Created on 07/01/2020
 */
public class LightEasyTestUtilTest {
    public static void testTestUtil() {
        TestUtil.equalInt(3, 3);
        TestUtil.equalInt(3, 3);
        TestUtil.reach(3);
        TestUtil.reach(3);
        TestUtil.equalInt(7, 6);
        TestUtil.reach(2);
        TestUtil.reach(3);
        TestUtil.reach(3);
    }


    public static void main(String[] args) {
        //expect 2 3 3 3
        //catch 3!=4
        testTestUtil();
    }
}
