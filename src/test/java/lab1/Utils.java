package lab1;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import picocli.CommandLine;
import vjvm.vm.Main;

public class Utils {
  private static final Map<DumpSection, BiFunction<String, String, DumpSection>> checkFunction = Map.ofEntries(
      entry(DumpSection.HEADER, Utils::checkHeader),
      entry(DumpSection.CONSTANT_POOL, Utils::checkConstantPool),
      entry(DumpSection.INTERFACES, Utils::checkInterfaces), entry(DumpSection.FIELDS, Utils::checkFields),
      entry(DumpSection.METHODS, Utils::checkMethods));

  private static final Pattern constantPattern = Pattern.compile("#(\\d+) = (\\w+):(?: (.+))?");

  static void assertDumpEquals(String expected, String actual) {
    var expectedReader = new BufferedReader(new StringReader(expected));
    var actualReader = new BufferedReader(new StringReader(actual));
    String expectedLine, actualLine;
    var currentSection = DumpSection.HEADER;

    while (true) {
      expectedLine = readNonBlankLine(expectedReader);
      actualLine = readNonBlankLine(actualReader);

      assertTrue(expectedLine != null || actualLine == null, "Excessive output");
      assertTrue(expectedLine == null || actualLine != null, "Missing output");

      if (expectedLine == null) {
        return;
      }

      expectedLine = expectedLine.strip();
      actualLine = actualLine.strip();
      currentSection = checkFunction.get(currentSection).apply(expectedLine, actualLine);
    }
  }

  static int runCmd(String path, String clazz) {
    var cmd = new CommandLine(new Main());
    var args = new ArrayList<String>();

    if (path != null) {
      args.add("-cp");
      args.add(path);
    }

    args.add("dump");
    args.add(clazz);
    return cmd.execute(args.toArray(String[]::new));
  }

  private static DumpSection checkHeader(String expected, String actual) {
    assertEquals(expected, actual);
    return expected.startsWith("constant pool:") ? DumpSection.CONSTANT_POOL : DumpSection.HEADER;
  }

  private static DumpSection checkConstantPool(String expected, String actual) {
    if (expected.startsWith("interfaces:")) {
      assertEquals(expected, actual);
      return DumpSection.INTERFACES;
    }

    var expectedVars = constantPattern.matcher(expected);
    var actualVars = constantPattern.matcher(expected);

    assertTrue(expectedVars.matches(), "BUG! Please report to TA");
    assertTrue(actualVars.matches(), "Invalid output");
    assertEquals(expectedVars.group(1), actualVars.group(1));
    assertEquals(expectedVars.group(2), actualVars.group(2));

    switch (expectedVars.group(2)) {
    case "Class", "Fieldref", "Methodref", "InterfaceMethodref", "String", "Utf8", "Integer", "Long", "NameAndType", "Float", "Double" -> {
      assertEquals(expectedVars.group(3), actualVars.group(3));
    }
    case "Unknown" -> {
      // skip check actual
    }
    default -> {
      assertTrue(false, "BUG! Please report to TA");
    }
    }

    return DumpSection.CONSTANT_POOL;
  }

  private static DumpSection checkInterfaces(String expected, String actual) {
    assertEquals(expected, actual);
    return expected.startsWith("fields:") ? DumpSection.FIELDS : DumpSection.INTERFACES;
  }

  private static DumpSection checkFields(String expected, String actual) {
    assertEquals(expected, actual);
    return expected.startsWith("methods:") ? DumpSection.FIELDS : DumpSection.METHODS;
  }

  private static DumpSection checkMethods(String expected, String actual) {
    assertEquals(expected, actual);
    return DumpSection.METHODS;
  }

  @SneakyThrows
  private static String readNonBlankLine(BufferedReader reader) {
    String s;
    while (true) {
      s = reader.readLine();
      if (s == null || !s.isBlank()) {
        return s;
      }
    }
  }

  private enum DumpSection {
    HEADER, CONSTANT_POOL, INTERFACES, FIELDS, METHODS
  }
}
