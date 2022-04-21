package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKEVIRTUAL extends Instruction {
  private final MethodInfo method;

  public INVOKEVIRTUAL(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var methodRef = (MethodRef) cp.constant(pc.ushort());
    this.method = methodRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();

    // select the method to call, see spec. 5.4.6
    var args = frame.stack().popSlots(method.argc() + 1);
    var method = this.method;
    if (!method.private_()) {
      var obj = args.address(0);
      var objClass = thread.context().heap().get(obj).type();
      method = objClass.vtableMethod(method.vtableIndex());
    }

    thread.context().interpreter().invoke(method, thread, args);
  }

  @Override
  public String toString() {
    return String.format("invokevirtual %s:%s:%s", method.jClass().name(), method.name(), method.descriptor());
  }
}
