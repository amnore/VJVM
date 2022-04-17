package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.ClassRef;

public class CHECKCAST extends Instruction {
  private final JClass jClass;

  public CHECKCAST(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var classRef = (ClassRef) cp.constant(pc.ushort());
    jClass = classRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var obj = stack.popAddress();
    if (obj == 0) {
      stack.pushAddress(obj);
      return;
    }

    var objClass = thread.context().heap().get(obj).type();
    if (!objClass.castableTo(jClass))
      throw new ClassCastException();
    stack.pushAddress(obj);
  }

  @Override
  public String toString() {
    return String.format("checkcase %s", jClass.name());
  }
}
