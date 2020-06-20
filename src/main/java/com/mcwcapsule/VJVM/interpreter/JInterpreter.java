package com.mcwcapsule.VJVM.interpreter;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

public class JInterpreter {
    private static JInterpreter instance = new JInterpreter();
    private Instruction[] dispatchTable;

    private JInterpreter() {
        // TODO: init dispatch table
    }

    public void run(JThread thread) {
        // TODO: run
    }

    public static JInterpreter getInstance() {
        return instance;
    }
}
