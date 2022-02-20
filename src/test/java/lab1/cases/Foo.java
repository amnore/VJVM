package lab1.cases;

import java.io.Closeable;

class Foo implements Closeable {
  int a = 1;
  static long b = 2;
  private String c = "foo";
  final float d = 3.0f;

  public void close() {}
}
