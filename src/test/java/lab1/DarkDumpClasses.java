package lab1;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

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
  void dumpInJDK() {
    Consumer<String> run = clazz -> utils.checkDump("", clazz);

    run.accept("java.lang.String");
  }
}
