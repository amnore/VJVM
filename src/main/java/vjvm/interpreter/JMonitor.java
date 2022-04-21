package vjvm.interpreter;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import lombok.RequiredArgsConstructor;
import lombok.var;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JFrame;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.vm.VMContext;

/**
 * JMonitor is the commandline debugger interface for VJVM.
 *
 * The following commands follows gdb convensions: - si, ni, bt
 */
@RequiredArgsConstructor
public class JMonitor {
  private final VMContext context;
  private final LineReader reader = LineReaderBuilder.builder().build();
  private final CommandLine commandLine = new CommandLine(new Main()).addSubcommand(new Info());

  private JThread currentThread;
  private MethodInfo currentMethod;

  public void enter(JThread thread) {
    printPosition(thread);
    currentThread = thread;
    currentMethod = thread.top().method();

    for (var ret = -1; ret != 0;) {
      String command;
      try {
        command = reader.readLine("> ");
      } catch (EndOfFileException e) {
        command = "";
      }

      if (command.isEmpty()) {
        var history = reader.getHistory();

        if (history.isEmpty()) {
          continue;
        }

        command = history.get(history.last());
      }

      ret = commandLine.execute(command.split(" "));
    }
  }

  private void printPosition(JThread thread) {
    var f = thread.top();
    var pc = thread.pc();
    var pos = pc.position();

    var instr = Instruction.decode(pc, f.method());
    pc.position(pos);

    if (thread.top().method() != currentMethod) {
      System.err.println(formatFrame(f));
    }
    System.err.printf("%-4d %s\n", pos, instr);
  }

  static String formatFrame(JFrame frame) {
    return String.format("%s:%s:%s", frame.jClass().name(), frame.method().name(), frame.method().descriptor());
  }

  @Command(description = "VJVM debugger interface")
  private class Main {
    @Command(name = "si", description = "step instruction")
    private int stepInstruction(@Parameters(arity = "0..1", defaultValue = "1") int steps) {
      context.interpreter().step(steps);
      return 0;
    }

    @Command(name = "c", description = "continue")
    private int continue_() {
      context.interpreter().continue_();
      return 0;
    }

    @Command(name = "bt", description = "backtrace")
    private int backtrace() {
      int i = 0;
      var frames = currentThread.frames();
      for (var it = frames.listIterator(frames.size()); it.hasPrevious(); i++) {
        var f = it.previous();
        System.err.printf("#%-4d %-4d in %s\n", i, f.pc().position(), formatFrame(f));
      }

      return -1;
    }

    @Command(name = "q", description = "quit")
    private int quit() {
      System.exit(0);
      return 0;
    }

    @Command(name = "b", description = "set breakpoint")
    private int breakpoint(@Parameters String location) {
      var loc = location.split(":");

      if (loc.length < 2 || loc.length > 3) {
        System.err.printf("Unknown location %s\n", location);
        return -1;
      }

      var clazz = loc[0];
      var method = loc[1];
      var descriptor = loc.length == 3 ? loc[2] : null;

      var jClass = context.userLoader().loadClass('L' + clazz + ';');
      if (jClass == null) {
        System.err.printf("Can not find class %s\n", clazz);
        return -1;
      }

      for (var i = 0; i < jClass.methodsCount(); i++) {
        var m = jClass.method(i);
        if (!m.name().equals(method) || (descriptor != null && !m.descriptor().equals(descriptor))) {
          continue;
        }

        if (m.native_()) {
          System.err.printf("Can not set breakpoint on native method\n");
          continue;
        }

        context.interpreter().setBreakpoint(m, 0);
      }

      return -1;
    }

    @Command(name = "d", description = "delete breakpoint")
    private int deleteBreakpoint(@Parameters int index) {
      context.interpreter().removeBreakpoint(index);
      return -1;
    }
  }

  @Command(name = "i", description = "info")
  private class Info {
    @Command(name = "lo", description = "print local variable slots")
    private int locals() {
      var locals = currentThread.top().vars();
      for (var i = 0; i < locals.size(); i++) {
        System.err.printf("#%-4d = 0x%x\n", i, locals.int_(i));
      }

      return -1;
    }

    @Command(name = "b", description = "print breakpoints")
    private int breakpoints() {
      var breakpoints = context.interpreter().breakpoints();
      for (var i = 0; i < breakpoints.size(); i++) {
        var bp = breakpoints.get(i);
        var method = bp.getLeft();
        System.err.printf("#%-4d at %s:%s:%s\n", i, method.jClass().name(), method.name(), method.descriptor());
      }

      return -1;
    }

    @Command(name = "op", description = "print operand stack")
    private int operandStack() {
      var stack = currentThread.top().stack();
      var slots = stack.slots();
      for (var i = 0; i < slots.size(); i++) {
        System.err.printf("#%-4d = 0x%-8x %s\n", i, slots.int_(i), i == stack.top() ? "<-- top" : "");
      }
      if (stack.top() == slots.size()) {
        System.err.printf("%19s%s\n", "", "<-- top");
      }
      return -1;
    }
  }
}
