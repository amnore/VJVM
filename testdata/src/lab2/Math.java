// Shamelessly taken from jvm19 (by xxz & wym)
package lab2;

public class Math {
  public static void test(int a, int b) {
    int c = a + b;
    if (c == 11 && a - b == 1 && a * b == 30 && a / b == 1) {
      IOUtil.writeInt(1);
    } else {
      IOUtil.writeInt(2);
    }
  }

  public static void main(String[] args) {
    test(6, 5);
  }
}
