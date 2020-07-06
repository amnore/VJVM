package com.mcwcapsule.VJVM.utils;

import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class ExceptionUtil {
    /**
     * Throw an exception at specified thread.
     * Before throwing, the pc of target thread must be set to the position the exception is thrown.
     *
     * @param exceptionRef reference of the exception object to throw
     * @param thread       the thread to throw exception at
     */
    public static void throwException(int exceptionRef, JThread thread) {
        val heap = VJVM.getHeap();
        val excClass = heap.getJClass(heap.getSlots().getInt(exceptionRef - 1));

        // unwind stack
        while (!thread.isEmpty()) {
            val frame = thread.getCurrentFrame();
            val method = frame.getMethodInfo();
            val stack = frame.getOpStack();
            val pc = frame.getPC();
            for (val handler : method.getCode().getExceptionTable()) {
                if (pc.position() < handler.getStartPC()
                    || pc.position() >= handler.getEndPC()
                    || (handler.getCatchType() != null && !excClass.canCastTo(handler.getCatchType())))
                    continue;

                // a matching handler is found
                pc.position(handler.getHandlerPC());
                stack.clear();
                stack.pushAddress(exceptionRef);
                return;
            }

            // no matching handler is found in current method
            thread.popFrame();
        }
    }
}
