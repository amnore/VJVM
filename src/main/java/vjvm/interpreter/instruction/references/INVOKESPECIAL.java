package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESPECIAL extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var methodRef = (MethodRef) frame.link().constant(thread.pc().ushort());
    var currentClass = frame.jClass();

    JClass targetClass;
    JClass refClass = methodRef.jClass();
    if (!methodRef.value().name().equals("<init>") && !refClass.interface_()
      && currentClass.subClassOf(refClass) && refClass.super_()) {
      targetClass = currentClass.superClass().value();
    } else {
      targetClass = methodRef.jClass();
    }

    var method = targetClass.vtableMethod(methodRef.value().vtableIndex());
    var args = frame.stack().popSlots(method.argc() + 1);
    thread.context().interpreter().invoke(method, thread, args);
  }

}
