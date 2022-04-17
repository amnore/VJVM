package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.ClassRef;

public class INSTANCEOF extends Instruction {
  private final JClass jClass;

  public INSTANCEOF(ProgramCounter pc, MethodInfo method) {
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
      stack.pushInt(0);
      return;
    }

    var objClass = thread.context().heap().get(obj).type();
    stack.pushInt(objClass.castableTo(jClass) ? 1 : 0);
  }

  @Override
  public String toString() {
    return String.format("instanceof %s", jClass.name());
  }
}
