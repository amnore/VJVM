package vjvm.interpreter.instruction.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.Constant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LDCX extends Instruction {
  private final Constant constant;
  private final String name;
  private final int index;

  public static LDCX LDC(ProgramCounter pc, MethodInfo method) {
    var index = pc.ubyte();
    var cp = method.jClass().constantPool();
    return new LDCX(cp.constant(index), "ldc", index);
  }

  public static LDCX LDC_W(ProgramCounter pc, MethodInfo method) {
    var index = pc.ushort();
    var cp = method.jClass().constantPool();
    return new LDCX(cp.constant(index), "ldc_w", index);
  }

  public static LDCX LDC2_W(ProgramCounter pc, MethodInfo method) {
    var index = pc.ushort();
    var cp = method.jClass().constantPool();
    return new LDCX(cp.constant(index), "ldc2_w", index);
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var value = constant.value();

    if (value instanceof Integer)
      stack.pushInt((Integer) value);
    else if (value instanceof Float)
      stack.pushFloat((Float) value);
    else if (value instanceof JClass)
      stack.pushAddress(((JClass) value).classObject().address());
    else if (value instanceof Long)
      stack.pushLong((Long) value);
    else if (value instanceof Double)
      stack.pushDouble((Double) value);
    else
      throw new Error(String.format("Unsupported: %s", value));
  }

  @Override
  public String toString() {
    return String.format("%s %d", name, index);
  }
}
