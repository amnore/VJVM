// Shamelessly taken from jvm19 (by xxz & wym)
package lab2;

public class Condition {
  public static void test(int small, int big, long smallL, long bigL, float smallF, float bigF, double smallD,
      double bigD) {
    if (small == 3) {
      if (small < big && smallL < bigL && smallF < bigF && smallD < bigD) {
        IOUtil.writeInt(1);
      } else {
        IOUtil.writeInt(2);
      }
      big++;
      if (big > small && bigL > smallL && bigF > smallF && bigD > smallD) {
        IOUtil.writeInt(3);
      } else {
        IOUtil.writeInt(4);
      }
    } else {
      IOUtil.writeInt(5);
    }

    if (small <= big) {
      if (big > small) {
        IOUtil.writeInt(6);
      } else {
        IOUtil.writeInt(7);
      }
      if (big + 1 >= small) {
        if (big == small) {
          IOUtil.writeInt(8);
        }
        if (big != small) {
          IOUtil.writeInt(9);
        } else {
          IOUtil.writeInt(10);
        }
      }
    }
  }

  public static void main(String[] args) {
    test(3, 4, 5, 6, 7f, 8f, 9, 10);
  }
}
