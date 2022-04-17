package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKEINTERFACE extends Instruction {
  private final MethodInfo method;

  public INVOKEINTERFACE(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var methodRef = (MethodRef) cp.constant(pc.ushort());

    // skip count and trailing zero
    pc.short_();
    this.method = methodRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var argc = method.argc();

    // select the method to call, see spec. 5.4.6
    var args = frame.stack().popSlots(argc + 1);
    var method = this.method;
    if (!method.private_()) {
      var obj = args.address(0);
      var objClass = thread.context().heap().get(obj).type();
      method = objClass.findMethod(method.name(), method.descriptor(), false);
    }

    thread.context().interpreter().invoke(method, thread, args);
  }

  @Override
  public String toString() {
    return String.format("invokeinterface %s", method.descriptor());
  }
}
