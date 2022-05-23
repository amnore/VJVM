// Shamelessly taken from jvm19 (by xxz & wym)
package lab2;

public class Jmp {
  public static void testJmp(boolean a, boolean b, boolean c) {
    if (a) {
      IOUtil.writeInt(0);
    } else {
      IOUtil.writeInt(1);
      if (b || c) {
        IOUtil.writeInt(2);
        if (c) {
          IOUtil.writeInt(3);
        } else {
          IOUtil.writeInt(4);
        }
      } else {
        IOUtil.writeInt(5);
      }
      IOUtil.writeInt(6);
    }
    IOUtil.writeInt(7);
  }

  public static void main(String[] args) {
    testJmp(false, false, true);
  }
}
