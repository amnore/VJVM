package com.mcwcapsule.VJVM.interpreter.instruction;

import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.ProgramCounter;

public abstract class Instruction {
    public abstract void fetchAndRun(JThread thread);
}
