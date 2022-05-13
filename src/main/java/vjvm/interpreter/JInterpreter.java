package vjvm.interpreter;

import lombok.Getter;
import lombok.var;

import org.apache.commons.lang3.tuple.Triple;
import vjvm.classfiledefs.Descriptors;
import vjvm.classfiledefs.MethodDescriptors;
import vjvm.classfiledefs.Opcodes;
import vjvm.interpreter.instruction.Decoder;
import vjvm.runtime.JFrame;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;
import vjvm.runtime.object.StringObject;
import vjvm.utils.InputUtils;

import java.util.*;
import java.util.function.BiFunction;

import static vjvm.classfiledefs.Descriptors.*;

public class JInterpreter {
  // (ClassName, MethodName, MethodDescriptor) -> HackFunction
  private static final HashMap<Triple<String, String, String>, BiFunction<JThread, Slots, Object>> nativeTable = new HashMap<>();

  @Getter
  private Status status = Status.CONTINUE;
  private long steps;

  private final ArrayList<Breakpoint> breakpoints = new ArrayList<>();
  private Breakpoint currentBreakpoint;

  /**
   * Invoke a method when there is no frames in a thread.
   *
   * @param method the method to call
   * @param thread the thread to run
   * @param args   the supplied arguments, index begins at 0
   */
  public void invoke(MethodInfo method, JThread thread, Slots args) {
    assert args.size() == method.argc() + (method.static_() ? 0 : 1);

    var frame = new JFrame(method, args);
    thread.push(frame);

    if (method.native_()) {
      runNativeMethod(thread);
    } else {
      run(thread);
    }
  }

  public void step(long steps) {
    assert steps >= 0;

    status = Status.STEP;
    this.steps = steps;
  }

  public void continue_() {
    status = Status.CONTINUE;
  }

  public void break_() {
    status = Status.BREAK;
  }

  public void setBreakpoint(MethodInfo method, int offset) {
    var code = method.code().code();
    var opcode = code[offset];

    if (opcode == Opcodes.OPC_breakpoint) {
      return;
    }

    var bp = new Breakpoint(method, offset);
    breakpoints.add(bp);
    bp.enable();
  }

  public void removeBreakpoint(int index) {
    disableBreakpoint(breakpoints.get(index));
    breakpoints.remove(index);
  }

  public List<Breakpoint> breakpoints() {
    return Collections.unmodifiableList(breakpoints);
  }

  private void disableBreakpoint(Breakpoint bp) {
  }

  private void findCurrentBreakpoint(JThread thread) {
    var method = thread.top().method();
    var offset = thread.pc().position();

    var bp = breakpoints.stream()
      .filter(t -> t.method().equals(method) && t.offset() == offset)
      .findFirst();

    if (!bp.isPresent()) {
      throw new Error("no breakpoint found");
    }

    currentBreakpoint = bp.get();
  }

  private void run(JThread thread) {
    var frame = thread.top();
    var monitor = thread.context().monitor();

    while (thread.top() == frame) {
      if (status == Status.STEP && steps == 0) {
        monitor.enter(thread);
      }

      var op = Decoder.decode(thread.pc(), frame.method());
      steps--;
      op.run(thread);

      if (currentBreakpoint != null) {
        currentBreakpoint.enable();
        currentBreakpoint = null;
      }

      if (status == Status.BREAK) {
        findCurrentBreakpoint(thread);
        currentBreakpoint.disable();
        monitor.enter(thread);
      }
    }
  }
  private void runNativeMethod(JThread thread) {
    var frame = thread.top();
    var method = frame.method();
    assert method.native_();

    var key = Triple.of(method.jClass().name(), method.name(), method.descriptor());
    var impl = nativeTable.get(key);
    if (impl == null) {
      // TODO: throw exception in thread
      throw new Error(String.format("Unimplemented native method: %s", key));
    }

    var ret = impl.apply(thread, frame.vars());
    thread.pop();
    var s = thread.top().stack();

    switch (MethodDescriptors.returnType(method.descriptor())) {
    case 'V':
      break;
    case DESC_array:
    case DESC_reference:
      s.pushAddress((Integer) ret);
      break;
    case DESC_boolean:
      s.pushInt(((Boolean) ret) ? 1 : 0);
      break;
    case DESC_byte:
      s.pushInt((Byte) ret);
      break;
    case DESC_char:
      s.pushInt((Character) ret);
      break;
    case DESC_double:
      s.pushDouble((Double) ret);
      break;
    case DESC_float:
      s.pushFloat((Float) ret);
      break;
    case DESC_int:
      s.pushInt((Integer) ret);
      break;
    case DESC_long:
      s.pushLong((Long) ret);
      break;
    case DESC_short:
      s.pushInt((Short) ret);
      break;
    default:
      throw new Error("Invalid return type");
    }
  }

  private boolean handleException(JThread thread) {
    var frame = thread.top();
    var exception = thread.exception();
    var eClass = exception.type();
    var method = frame.method();
    var pc = frame.pc();
    var stack = frame.stack();

    for (var handler : method.code().exceptionTable()) {
      if (pc.position() < handler.startPC() || pc.position() >= handler.endPC()
          || (handler.catchType() != null && !eClass.castableTo(handler.catchType())))
        continue;

      // a matching handler is found
      pc.position(handler.handlerPC());
      stack.clear();
      stack.pushAddress(exception.address());
      thread.exception(null);
      return true;
    }

    return false;
  }

  public static enum Status {
    CONTINUE, STEP, BREAK,
  }

  static {
    nativeTable.put(Triple.of("java/lang/Object", "registerNatives", "()V"), (t, a) -> null);
    nativeTable.put(Triple.of("java/lang/Class", "registerNatives", "()V"), (t, a) -> null);
    nativeTable.put(Triple.of("java/lang/Class", "desiredAssertionStatus0", "(Ljava/lang/Class;)Z"), (t, a) -> true);
    nativeTable.put(Triple.of("java/lang/String", "intern", "()Ljava/lang/String;"), (t, a) -> {
      var h = t.context().heap();
      var s = (StringObject) t.context().heap().get(a.address(0));
      return h.intern(s);
    });
    nativeTable.put(Triple.of("java/lang/Throwable", "fillInStackTrace", "(I)Ljava/lang/Throwable;"),
        (t, a) -> a.address(0));
    nativeTable.put(Triple.of("java/lang/Class", "getPrimitiveClass", "(Ljava/lang/String;)Ljava/lang/Class;"),
        (t, a) -> {
          var c = t.context();
          var str = (StringObject) c.heap().get(a.address(0));
          var desc = Descriptors.of(str.value());
          return c.bootstrapLoader().loadClass(desc).classObject().address();
        });
    nativeTable.put(Triple.of("java/lang/Float", "floatToRawIntBits", "(F)I"), (t, a) -> a.int_(0));
    nativeTable.put(Triple.of("java/lang/Double", "doubleToRawLongBits", "(D)J"), (t, a) -> a.long_(0));
    nativeTable.put(Triple.of("java/lang/Double", "longBitsToDouble", "(J)D"), (t, a) -> a.double_(0));
    nativeTable.put(Triple.of("java/lang/System", "registerNatives", "()V"), (t, a) -> null);
    nativeTable.put(Triple.of("java/lang/StrictMath", "sin", "(D)D"), (t, a) -> Math.sin(a.double_(0)));
    nativeTable.put(Triple.of("java/lang/StrictMath", "exp", "(D)D"), (t, a) -> Math.exp(a.double_(0)));
    nativeTable.put(Triple.of("java/lang/StrictMath", "pow", "(DD)D"), (t, a) -> Math.pow(a.double_(0), a.double_(2)));
    nativeTable.put(Triple.of("java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V"), (t, a) -> {
      // only support char[], no checks
      var heap = t.context().heap();
      var src = (ArrayObject) heap.get(a.address(0));
      var srcPos = a.int_(1);
      var dest = (ArrayObject) heap.get(a.address(2));
      var destPos = a.address(3);
      var length = a.int_(4);

      for (int i = 0; i < length; ++i) {
        dest.char_(destPos + 1, src.char_(srcPos + i));
      }
      return null;
    });

    nativeTable.put(Triple.of("lab2/IOUtil", "readInt", "()I"), (t, a) -> InputUtils.readInt());
    nativeTable.put(Triple.of("lab2/IOUtil", "readLong", "()J"), (t, a) -> InputUtils.readLong());
    nativeTable.put(Triple.of("lab2/IOUtil", "readChar", "()C"), (t, a) -> InputUtils.readChar());
    nativeTable.put(Triple.of("lab2/IOUtil", "writeInt", "(I)V"), (t, a) -> {
      System.out.println(a.int_(0));
      return null;
    });
    nativeTable.put(Triple.of("lab2/IOUtil", "writeFloat", "(F)V"), (t, a) -> {
      System.out.println(a.float_(0));
      return null;
    });
    nativeTable.put(Triple.of("lab2/IOUtil", "writeLong", "(J)V"), (t, a) -> {
      System.out.println(a.long_(0));
      return null;
    });
    nativeTable.put(Triple.of("lab2/IOUtil", "writeDouble", "(D)V"), (t, a) -> {
      System.out.println(a.double_(0));
      return null;
    });
    nativeTable.put(Triple.of("lab2/IOUtil", "writeChar", "(C)V"), (t, a) -> {
      System.out.println(a.char_(0));
      return null;
    });
  }
}
