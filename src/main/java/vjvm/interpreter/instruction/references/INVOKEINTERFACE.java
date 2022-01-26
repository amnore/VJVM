package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;
import vjvm.vm.VMContext;

public class INVOKEINTERFACE extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var pc = thread.pc();
        var methodRef = (MethodRef) frame.dynLink().constant(pc.ushort());
        var argc = methodRef.argc();

        // skip count and trailing zero
        pc.short_();

        // select the method to call, see spec. 5.4.6
        MethodInfo method;
        if (methodRef.info().private_())
            method = methodRef.info();
        else {
            var heap = thread.context().heap();
            var stack = frame.opStack();
            var obj = stack.slots().address(stack.top() - methodRef.argc() - 1);
            var objClass = heap.get(obj).type();
            method = objClass.findMethod(methodRef.name(), methodRef.descriptor(), false);
        }
        InvokeUtil.invokeMethod(method, thread);
    }

}
