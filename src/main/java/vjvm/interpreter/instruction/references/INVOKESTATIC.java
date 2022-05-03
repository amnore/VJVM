package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESTATIC extends Instruction {
  private final JClass jClass;
  private final MethodInfo method;

  public INVOKESTATIC(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var methodRef = (MethodRef) cp.constant(pc.ushort());
    jClass = methodRef.jClass();
    this.method = methodRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();

    jClass.initialize(thread);

    var args = frame.stack().popSlots(method.argc());
    thread.context().interpreter().invoke(method, thread, args);
  }

  @Override
  public String toString() {
    return String.format("invokestatic %s:%s:%s", method.jClass().name(), method.name(), method.descriptor());
  }
}
