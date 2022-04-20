package lab1;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.var;
import org.junit.jupiter.api.Test;
import vjvm.vm.VMContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DarkDumpClasses {
  final DumpClasses utils = new DumpClasses();

  @Test
  void dumpInDir() {
    Consumer<String> run = clazz -> utils.checkDump(utils.resPath.toString(), "lab1.cases." + clazz);

    run.accept("A");
    run.accept("A$B");
    run.accept("A$C");
  }

  @Test
  void dumpSame1() {
    Consumer<String> run = clazz -> utils.checkDump(utils.jarPath.toString(), "lab1.cases." + clazz);

    run.accept("jar.Same");
    assertEquals(-1, Utils.runCmd(utils.jarPath.toString(), "lab1.cases.Same"));
  }

  @Test
  void dumpSame2() {
    Consumer<String> run = clazz -> utils.checkDump(utils.resPath.toString(), "lab1.cases." + clazz);

    run.accept("Same");
    assertEquals(-1, Utils.runCmd(utils.resPath.toString(), "lab1.cases.jar.Same"));
  }

  @Test
  void dumpJDKString() {
    Consumer<String> run = clazz -> utils.checkDump(utils.resPath.toString(), "java.lang." + clazz);

    run.accept("String");
  }

  @Test
  void dumpOurString() {
    Consumer<String> run = clazz -> utils.checkDump(utils.resPath.toString(), "lab1.cases." + clazz);

    run.accept("String");
  }

  @Test
  void dumpInJDK() {
    Consumer<String> run = clazz -> utils.checkDump("", clazz);
    run.accept("java.lang.String");
  }

}
