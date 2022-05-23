// Shamelessly taken from jvm19 (by xxz & wym)
package lab2;

public class Conversion {
  public static void test(float flt, double db, double bigDB, float bigFLT) {
    // d2f
    float a = (float) db;
    float b = flt;
    if (a == b) {
      IOUtil.writeInt(0);
    }
    // d2i
    int c = (int) db;
    // f2i
    int d = (int) flt;
    if (c == d) {
      IOUtil.writeInt(1);
    }
    if (c == 3) {
      IOUtil.writeInt(2);
    }
    // d2i
    int max = (int) bigDB;
    if (max == Integer.MAX_VALUE) {
      IOUtil.writeInt(3);
    }
    // d2L
    long maxL = (long) bigDB;
    if (maxL != 2147483648L) {
      IOUtil.writeInt(4);
    }
    // f2i
    if (max != (int) bigFLT) {
      IOUtil.writeInt(5);
    }
    // f2l
    if (maxL != (long) bigFLT) {
      IOUtil.writeInt(6);
    }

    int toB;
    if (c == c) {
      toB = 128;
    } else {
      toB = 128;
    }
    // i2b
    byte bt = (byte) toB;
    if (bt == -128) {
      IOUtil.writeInt(7);
    }
    // i2c
    char ch = (char) toB;
    if (ch == 128) {
      IOUtil.writeInt(8);
    }
    // i2d
    if ((int) ((double) toB + 0.0) == toB) {
      IOUtil.writeInt(9);
    }
    // i2l & l2i
    if ((int) ((long) toB + ch + bt) == toB) {
      IOUtil.writeInt(10);
    }
    // l2d
    if ((int) ((double) ((long) toB + ch + bt) + 0.0) == toB) {
      IOUtil.writeInt(11);
    }
    // l2f
    if ((int) ((float) ((long) toB + ch + bt) + 0.0f) == toB) {
      IOUtil.writeInt(12);
    }
    toB <<= 8;
    // i2s
    short sh = (short) toB;
    if (sh == -toB) {
      IOUtil.writeInt(13);
    }
  }

  public static void main(String[] args) {
    test(3.99f, 3.99, 2147483648.0, 2147483648.0f);
  }
}
