package lab1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import vjvm.vm.Main;

class FindClasses {
  final Path resPath = Path.of(System.getenv("VJVM_TESTRES_PATH"));
  final Path jarPath = resPath.resolve("lab1/cases/jar.jar");

  @Test
  void findInDir() {
    Function<String, Integer> exec = (c) -> {
      var cmd = new CommandLine(new Main());
      var args = new String[] { "-cp", resPath.toString(), "dump", c };
      return cmd.execute(args);
    };

    assertEquals(0, exec.apply("lab1.cases.Foo"));
    assertNotEquals(0, exec.apply("lab1.cases.None"));
    assertNotEquals(0, exec.apply("lab1.cases.jar.Bar"));
  }

  @Test
  void findInJar() {
    Function<String, Integer> exec = (c) -> {
      var cmd = new CommandLine(new Main());
      var args = new String[] { "-cp", jarPath.toString(), "dump", c };
      return cmd.execute(args);
    };

    assertEquals(0, exec.apply("lab1.cases.jar.Bar"));
    assertNotEquals(0, exec.apply("lab1.cases.Foo"));
    assertNotEquals(0, exec.apply("lab1.cases.jar.None"));
  }
}
