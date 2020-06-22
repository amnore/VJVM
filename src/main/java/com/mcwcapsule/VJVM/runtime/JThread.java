package com.mcwcapsule.VJVM.runtime;

import lombok.Getter;

import java.util.Stack;

public class JThread {
    private final Stack<JFrame> frames = new Stack<>();
    @Getter
    private ProgramCounter PC;

    public JFrame getCurrentFrame() {
        return frames.lastElement();
    }

    public void popFrame() {
        frames.pop();
        PC = frames.empty() ? null : frames.lastElement().getPC();
    }

    public void pushFrame(JFrame frame) {
        frames.push(frame);
        PC = frame.getPC();
    }

    public boolean isEmpty() {
        return frames.empty();
    }
}
