package lab2;

public class Arith {
  public static void main(String[] args) {
    float a = 3.14f, b = 2.72f;
    IOUtil.writeFloat(a + b);
    IOUtil.writeFloat(a - b);
    IOUtil.writeFloat(a * b);
    IOUtil.writeFloat(a / b);
    IOUtil.writeFloat(a % b);
    IOUtil.writeFloat(-a);

    double c = -9.28, d = 4.03;
    IOUtil.writeDouble(c + d);
    IOUtil.writeDouble(c - d);
    IOUtil.writeDouble(c * d);
    IOUtil.writeDouble(c / d);
    IOUtil.writeDouble(c % d);
    IOUtil.writeDouble(-c);

    int e = -123, f = 4;
    IOUtil.writeInt(e + f);
    IOUtil.writeInt(e - f);
    IOUtil.writeInt(e * f);
    IOUtil.writeInt(e / f);
    IOUtil.writeInt(e % f);
    IOUtil.writeInt(-e);
    IOUtil.writeInt(e << f);
    IOUtil.writeInt(e >> f);
    IOUtil.writeInt(e >>> f);
    IOUtil.writeInt(e & f);
    IOUtil.writeInt(e | f);
    IOUtil.writeInt(e ^ f);
    IOUtil.writeInt(~e);

    long g = 999999999999L, h = 2147483647L;
    IOUtil.writeLong(g + h);
    IOUtil.writeLong(g - h);
    IOUtil.writeLong(g * h);
    IOUtil.writeLong(g / h);
    IOUtil.writeLong(g % h);
    IOUtil.writeLong(-g);
    IOUtil.writeLong(g << 1);
    IOUtil.writeLong(g >> 2);
    IOUtil.writeLong(g >>> 3);
    IOUtil.writeLong(g & h);
    IOUtil.writeLong(g | h);
    IOUtil.writeLong(g ^ h);
    IOUtil.writeLong(~g);
  }
}
