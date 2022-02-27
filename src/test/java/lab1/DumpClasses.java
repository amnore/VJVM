package lab1;

import lombok.var;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

public class DumpClasses {
  final Path resPath = FileSystems.getDefault().getPath(System.getenv("VJVM_TESTRES_PATH"));
  final Path resultPath = FileSystems.getDefault().getPath(System.getenv("VJVM_TESTRESULT_PATH"));
  final Path jarPath = resPath.resolve("lab1/cases/jar.jar");

  @Test
  void dumpInDir() {
    Consumer<String> run = clazz -> checkDump(resPath.toString(), "lab1.cases." + clazz);

    run.accept("A");
    run.accept("A$B");
    run.accept("A$C");
    run.accept("Foo");
  }

  @Test
  void dumpInJar() {
    Consumer<String> run = clazz -> checkDump(jarPath.toString(), "lab1.cases." + clazz);

    run.accept("jar.Bar");
  }

  @Test
  void dumpInJDK() {
    Consumer<String> run = clazz -> checkDump("", clazz);

    run.accept("java.lang.String");
  }

  @SneakyThrows
  void checkDump(String path, String clazz) {
    var expected = new String(Files.readAllBytes(resultPath.resolve(clazz.replace('.', '/') + ".dump")));

    // capture stdout
    var oldStdout = System.out;
    var stdout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(stdout));

    assertEquals(0, Utils.runCmd(path, clazz));
    System.setOut(oldStdout);

    Utils.assertDumpEquals(expected, stdout.toString());
  }
}
