package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.utils.ExceptionUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;

public class ATHROW extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var obj = thread.getCurrentFrame().getOpStack().popAddress();

        // if the reference is null, throw an NullPointerException instead
        if (obj == 0) {
            try {
                val nptrClass = VJVM.getBootstrapLoader().loadClass("java/lang/NullPointerException");
                obj = nptrClass.createInstance();
            } catch (ClassNotFoundException e) {
                throw new Error(e);
            }
        }

        thread.getPC().move(-1);
        ExceptionUtil.throwException(obj, thread);
    }
}
