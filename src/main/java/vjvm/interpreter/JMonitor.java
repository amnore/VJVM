package vjvm.interpreter;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import lombok.RequiredArgsConstructor;
import lombok.var;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Decoder;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JFrame;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.utils.UnimplementedInstructionError;
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
        commandLine.execute("q");
        continue;
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

    var instr = Decoder.decode(pc, f.method());
    pc.position(pos);

    if (thread.top().method() != currentMethod) {
      System.err.println(formatFrame(f));
    }
    System.err.printf("%-4d %s\n", pos, instr);
  }

  static String formatFrame(JFrame frame) {
    return String.format("%s:%s:%s", frame.jClass().name(), frame.method().name(), frame.method().descriptor());
  }

  @Command(description = "VJVM debugger interface", name = "")
  private class Main {
    @Command(name = "h", description = "print help message")
    private int help() {
      commandLine.usage(System.err);
      return -1;
    }

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
    private int breakpoint(@Parameters(paramLabel = "class") String clazz,
        @Parameters(paramLabel = "method") String method,
        @Parameters(paramLabel = "offset", arity = "0..1", defaultValue = "0") int offset) {
      var jClass = context.userLoader().loadClass(Descriptors.of(clazz));
      if (jClass == null) {
        System.err.printf("Can not find class %s\n", clazz);
        return -1;
      }

      for (var i = 0; i < jClass.methodsCount(); i++) {
        var m = jClass.method(i);
        if (!m.name().equals(method)) {
          continue;
        }

        if (m.native_()) {
          System.err.printf("Can not set breakpoint on native method\n");
          continue;
        }

        if (m.code().code().length <= offset) {
          continue;
        }

        context.interpreter().setBreakpoint(m, offset);
      }

      return -1;
    }

    @Command(name = "d", description = "delete breakpoint")
    private int deleteBreakpoint(@Parameters int index) {
      context.interpreter().removeBreakpoint(index);
      return -1;
    }

    @Command(name = "disas", description = "disassemble current function")
    private int disassemble() {
      var code = currentMethod.code().code();
      var pc = new ProgramCounter(code);
      var currentPC = currentThread.pc();
      var bpMap = context.interpreter().breakpoints().stream().filter(bp -> bp.method().equals(currentMethod))
          .collect(Collectors.toMap(bp -> bp.offset(), Function.identity()));

      while (pc.position() < code.length) {
        var pos = pc.position();
        Instruction op;

        try {
          var bp = bpMap.get(pc.position());
          if (bp != null) {
            op = Decoder.decode(new ProgramCounter(bp.instruction()), currentMethod);
            pc.move(bp.instruction().length);
          } else {
            op = Decoder.decode(pc, currentMethod);
          }
        } catch (UnimplementedInstructionError e) {
          pc.position(pos);
          break;
        }

        if (pos == currentPC.position()) {
          System.err.printf("%-4s %s\n", String.format("%d*", pos), op);
        } else {
          System.err.printf("%-4d %s\n", pos, op);
        }
      }

      // print instructions following the first that can not be decoded
      if (pc.position() < code.length) {
        System.err.printf("%-4d %s", pc.position(), StringUtils.repeat(' ', pc.position() % 4 * 3));
        do {
          if (pc.position() % 4 == 0) {
            System.err.printf("\n%-4d ", pc.position());
          }
          System.err.printf("%02x ", pc.ubyte());
        } while (pc.position() < code.length);
        System.err.println();
      }

      return -1;
    }
  }

  @Command(name = "i", description = "info")
  private class Info {
    @Command(name = "lo", description = "print local variable slots")
    private int locals() {
      var locals = currentThread.top().vars();
      for (var i = 0; i < locals.size(); i++) {
        var value = locals.value(i);
        if (value != null) {
          System.err.printf("#%-4d = %s\n", i, value);
        }
      }

      return -1;
    }

    @Command(name = "b", description = "print breakpoints")
    private int breakpoints() {
      var breakpoints = context.interpreter().breakpoints();
      for (var i = 0; i < breakpoints.size(); i++) {
        var bp = breakpoints.get(i);
        var method = bp.method();
        System.err.printf("#%-4d at %d in %s:%s:%s\n", i, bp.offset(), method.jClass().name(), method.name(),
            method.descriptor());
      }

      return -1;
    }

    @Command(name = "op", description = "print operand stack")
    private int operandStack() {
      var stack = currentThread.top().stack();
      var slots = stack.slots();
      for (var i = 0; i < stack.top(); i++) {
        var value = slots.value(i);
        if (value != null) {
          System.err.printf("#%-4d = %s\n", i, value);
        }
      }
      return -1;
    }
  }
}
