package lab2;

import lombok.SneakyThrows;
import lombok.var;
import picocli.CommandLine;
import vjvm.vm.Main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
  static final Path resPath = FileSystems.getDefault().getPath(System.getenv("VJVM_TESTRES_PATH"));
  static final Path resultPath = FileSystems.getDefault().getPath(System.getenv("VJVM_TESTRESULT_PATH"));
  static final Path inputPath = FileSystems.getDefault().getPath(System.getenv("VJVM_TESTINPUT_PATH"));

  @SneakyThrows
  public static void runClass(String name) {
    var cmd = new CommandLine(new Main());
    var args = new ArrayList<String>();
    var outFile = resultPath.resolve(name.replace('.', '/') + ".dump");
    var expected = new String(Files.readAllBytes(outFile));
    var input = new String(Files.readAllBytes(inputPath.resolve(name.replace('.', '/') + ".in")));

    args.add("-cp");
    args.add(resPath.toString());

    args.add("run");
    args.add(name);

    var oldStdout = System.out;
    var stdout = new ByteArrayOutputStream();
    var oldIn = System.in;
    var stdin = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    System.setOut(new PrintStream(stdout));
    System.setIn(stdin);

    assertEquals(0, cmd.execute(args.toArray(new String[0])));
    System.setOut(oldStdout);
    System.setIn(oldIn);

    assertOutputEquals(expected, stdout.toString());
  }

  private static void assertOutputEquals(String expected, String actual) {
    var expectedLines = expected.split("\r\n|\r|\n");
    var actualLines = actual.split("\r\n|\r|\n");

    assertEquals(String.join("\n", expectedLines), String.join("\n", actualLines));
  }
}
