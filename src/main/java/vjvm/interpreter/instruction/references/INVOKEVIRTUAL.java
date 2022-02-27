package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKEVIRTUAL extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var methodRef = (MethodRef) frame.link().constant(thread.pc().ushort());

    // select the method to call, see spec. 5.4.6
    MethodInfo method = methodRef.value();
    var args = frame.stack().popSlots(method.argc() + 1);
    if (!method.private_()) {
      var obj = args.address(0);
      var objClass = thread.context().heap().get(obj).type();
      method = objClass.vtableMethod(methodRef.value().vtableIndex());
    }

    thread.context().interpreter().invoke(method, thread, args);
  }

}
