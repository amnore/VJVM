package lab2_1;

import org.junit.jupiter.api.Test;

public class RunClassesTest {
  @Test
  public void runIOUtil() {
    Utils.runClss("lab2.IOUtil");
  }

  @Test
  public void runHelloWorld() {
    Utils.runClss("lab2.HelloWorld");
  }

  @Test
  public void runAdd() {
    Utils.runClss("lab2.Add");
  }

  @Test
  public void runArith() {
    Utils.runClss("lab2.Arith");
  }

  @Test
  public void runControl() {
    Utils.runClss("lab2.Control");
  }

  @Test
  public void runConv() {
    Utils.runClss("lab2.Conv");
  }

  @Test
  public void runDummy() {
    Utils.runClss("lab2.Dummy");
  }
}
