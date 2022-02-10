package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKEINTERFACE extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var pc = thread.pc();
    var methodRef = (MethodRef) frame.link().constant(pc.ushort());
    var argc = methodRef.value().argc();

    // skip count and trailing zero
    pc.short_();

    // select the method to call, see spec. 5.4.6
    MethodInfo method = methodRef.value();
    var args = frame.stack().popSlots(argc + 1);
    if (!method.private_()) {
      var obj = args.address(0);
      var objClass = thread.context().heap().get(obj).type();
      method = objClass.findMethod(method.name(), method.descriptor(), false);
    }

    thread.context().interpreter().invoke(method, thread, args);
  }

}
