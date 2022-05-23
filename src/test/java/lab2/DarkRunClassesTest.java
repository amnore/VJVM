package lab2;

import org.junit.jupiter.api.Test;

public class DarkRunClassesTest {
  @Test
  public void runSqrt() {
    TestUtils.runClass("lab2.Sqrt");
  }

  @Test
  public void runPrime() {
    TestUtils.runClass("lab2.Prime");
  }

  @Test
  public void runFibonacci() {
    TestUtils.runClass("lab2.Fibonacci");
  }

}
