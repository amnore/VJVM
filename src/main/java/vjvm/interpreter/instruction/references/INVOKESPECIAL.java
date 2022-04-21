package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESPECIAL extends Instruction {
  private final MethodInfo method;
  private final JClass refClass;
  private final JClass currentClass;

  public INVOKESPECIAL(ProgramCounter pc, MethodInfo method) {
    currentClass = method.jClass();

    var cp = currentClass.constantPool();
    var methodRef = (MethodRef) cp.constant(pc.ushort());

    this.method = methodRef.value();
    refClass = methodRef.jClass();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();

    JClass targetClass = refClass;
    if (!method.name().equals("<init>") && !refClass.interface_()
      && currentClass.subClassOf(refClass) && refClass.super_()) {
      targetClass = currentClass.superClass().value();
    }

    var method = targetClass.vtableMethod(this.method.vtableIndex());
    var args = frame.stack().popSlots(method.argc() + 1);
    thread.context().interpreter().invoke(method, thread, args);
  }

  @Override
  public String toString() {
    return String.format("invokespecial %s:%s:%s", method.jClass().name(), method.name(), method.descriptor());
  }
}
