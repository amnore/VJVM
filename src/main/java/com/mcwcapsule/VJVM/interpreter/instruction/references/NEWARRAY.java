package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class NEWARRAY extends Instruction {
    private static final String[] arrType = {
        null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
    };

    @Override
    public void fetchAndRun(JThread thread) {
        val atype = thread.getPC().getUnsignedByte();
        ArrayClass jClass;
        try {
            jClass = (ArrayClass) VJVM.getBootstrapLoader().loadClass(arrType[atype]);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        if (jClass.getInitState() != JClass.InitState.INITIALIZED) {
            thread.getPC().move(-2);
            jClass.tryInitialize(thread);
        }
        val stack =thread.getCurrentFrame().getOpStack();
        val ref = jClass.createInstance(stack.popInt());
        stack.pushAddress(ref);
    }

}
