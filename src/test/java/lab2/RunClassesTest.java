package lab2;

import org.junit.jupiter.api.Test;

public class RunClassesTest {
  @Test
  public void runIOUtil() {
    TestUtils.runClass("lab2.IOUtil");
  }

  @Test
  public void runHelloWorld() {
    TestUtils.runClass("lab2.HelloWorld");
  }

  @Test
  public void runControl() {
    TestUtils.runClass("lab2.Control");
  }

  @Test
  public void runConv() {
    TestUtils.runClass("lab2.Conv");
  }

  @Test
  public void runDummy() {
    TestUtils.runClass("lab2.Dummy");
  }

  @Test
  public void runJmp() {
    TestUtils.runClass("lab2.Jmp");
  }

  @Test
  public void runCondition() {
    TestUtils.runClass("lab2.Condition");
  }

  @Test
  public void runMath() {
    TestUtils.runClass("lab2.Math");
  }

  @Test
  public void runConversion() {
    TestUtils.runClass("lab2.Conversion");
  }

  @Test
  public void runArith() {
    TestUtils.runClass("lab2.Arith");
  }

  @Test
  public void runAdd() {
    TestUtils.runClass("lab2.Add");
  }
}
