package cases.light;


import cases.TestUtil;

/**
 * Description:
 *
 * @author xxz
 * Created on 07/01/2020
 */
public class LightEasyBranchTest {

    public static void testJmp(boolean a, boolean b, boolean c) {
        //ifeq
        if (a) {
            TestUtil.reach(-5);
        } else {
            //ifne ifeq
            if (b || c) {
                //ifeq
                if (c) {
                    //goto
                    //return
                } else {
                    TestUtil.reach(-5);
                }
            } else {
                TestUtil.reach(-5);
            }
        }

    }

    public static void test(int small, int big, long smallL, long bigL, float smallF, float bigF,
                            double smallD, double bigD) {
        if (small == 4) {
            if (small < big && smallL < bigL && smallF < bigF && smallD < bigD) {

            } else {
                TestUtil.reach(-3);
            }
            big++;
            if (big > small && bigL > smallL && bigF > smallF && bigD > smallD) {

            } else {
                TestUtil.reach(-4);
            }
        } else {
            TestUtil.reach(-5);
        }


        if (small <= big) {
            if (big > small) {

            } else {
                TestUtil.reach(-1);
            }
            if (big + 1 >= small) {
                if (big == small) {
                    TestUtil.reach(-1);
                }
                if (big != small) {

                } else {
                    TestUtil.reach(-1);
                }
            }
        }
    }


    public static void main(String[] args) {
        TestUtil.reach(-3);
        testJmp(false, false, true);
        test(4, 5, 6, 7, 8f, 9, 10, 11);
        TestUtil.reach(-4);
    }
}
