package lab2;

public class Add {
  public static void main(String[] args) {
    int a = 1, b = 2;
    byte c = -1, d = 1;
    short e = 32767, f = 1;
    char g = 0;
    int h = 2147483647;
    float i = 1.0f, j = -1.0f;
    double k = 1e300, l = 1;
    long m = 9999999999999999L, n = 111111111111111L;

    IOUtil.writeInt(a + b);
    IOUtil.writeInt(c + d);
    IOUtil.writeInt(e + f);
    IOUtil.writeInt(g - a);
    IOUtil.writeInt(h + a);
    IOUtil.writeFloat(i + j);
    IOUtil.writeDouble(k + l);
    IOUtil.writeLong(m + n);
  }
}
