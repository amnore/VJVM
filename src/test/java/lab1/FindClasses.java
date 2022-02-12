package lab1;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import vjvm.vm.Main;

import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FindClasses {
  final Path resPath = Path.of(System.getenv("VJVM_TESTRES_PATH"));
  final Path jarPath = resPath.resolve("lab1/cases/jar.jar");

  @Test
  void findInDir() {
    Function<String, Integer> exec = (c) -> {
      var cmd = new CommandLine(new Main());
      var args = new String[]{"-cp", resPath.toString(), "dump",
        "lab1.cases." + c};
      return cmd.execute(args);
    };

    assertEquals(0, exec.apply("Foo"));
    assertEquals(0, exec.apply("A"));
    assertEquals(0, exec.apply("A$B"));
    assertEquals(0, exec.apply("A$C"));
    assertNotEquals(0, exec.apply("None"));
    assertNotEquals(0, exec.apply("jar.Bar"));
  }

  @Test
  void findInJar() {
    Function<String, Integer> exec = (c) -> {
      var cmd = new CommandLine(new Main());
      var args = new String[]{"-cp", jarPath.toString(), "dump",
        "lab1.cases." + c};
      return cmd.execute(args);
    };

    assertEquals(0, exec.apply("jar.Bar"));
    assertNotEquals(0, exec.apply("Foo"));
    assertNotEquals(0, exec.apply("jar.None"));
  }
}
