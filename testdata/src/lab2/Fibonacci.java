package lab2;

public class Fibonacci {
  public static void main(String[] args) {
    long a = 0, b = 1;
    for (int i = 0; i < 80; i++) {
      long c = a + b;
      a = b;
      b = c;
      IOUtil.writeLong(a);
    }
  }
}
