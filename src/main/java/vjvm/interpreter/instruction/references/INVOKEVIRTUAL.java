package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.MethodRef;
import vjvm.utils.InvokeUtil;
import vjvm.vm.VJVM;
import lombok.val;

public class INVOKEVIRTUAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        // select the method to call, see spec. 5.4.6
        MethodInfo method;
        if (methodRef.getInfo().isPrivate())
            method = methodRef.getInfo();
        else {
            val heap = VJVM.getHeap();
            val stack = frame.getOpStack();
            val obj = stack.getSlots().getAddress(stack.getTop() - methodRef.getArgc() - 1);
            val objClass = heap.getJClass(heap.getSlots().getInt(obj - 1));
//            method = objClass.findMethod(methodRef.getName(), methodRef.getDescriptor());
            method = objClass.getVtableMethod(methodRef.getInfo().getVtableIndex());
        }
        InvokeUtil.invokeMethod(method, thread);
    }

}
