package lab1;

import lombok.var;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import vjvm.vm.Main;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static lab1.Utils.runCmd;

class DarkFindClasses {
  final FindClasses utils = new FindClasses();

  @Test
  void findInDir() {
    Function<String, Integer> exec = (c) -> runCmd(utils.resPath.toString(), "lab1.cases." + c);

    assertEquals(0, exec.apply("A"));
    assertEquals(0, exec.apply("A$B"));
    assertEquals(0, exec.apply("A$C"));
    assertNotEquals(0, exec.apply("None"));
    assertNotEquals(0, exec.apply("jar.Bar"));
  }

  @Test
  void findInJar() {
    Function<String, Integer> exec = (c) -> runCmd(utils.jarPath.toString(), "lab1.cases." + c);

    assertNotEquals(0, exec.apply("Foo"));
    assertNotEquals(0, exec.apply("jar.None"));
  }

  @Test
  void findInJDK() {
    Function<String, Integer> exec = (c) -> runCmd(null, c);

    assertEquals(0, exec.apply("java.lang.String"));
    assertNotEquals(0, exec.apply("java.lang.None1234"));
  }
}
