package lab2;

public class Sqrt {
  public static double sqrt(double d) {
    double p = 0.5;
    for (int i = 0; i < 10; i++) {
      p = (p + d / p) / 2;
    }
    return p;
  }

  public static void main(String[] args) {
    for (double i = 1; i <= 100; i += 1) {
      IOUtil.writeDouble(sqrt(i));
    }
  }
}
