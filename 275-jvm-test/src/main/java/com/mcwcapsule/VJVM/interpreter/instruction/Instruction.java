package com.mcwcapsule.VJVM.interpreter.instruction;

import com.mcwcapsule.VJVM.runtime.JThread;

public abstract class Instruction {
    public abstract void fetchAndRun(JThread thread);
}
