package com.mcwcapsule.VJVM.runtime;

import java.util.Stack;

import lombok.Getter;

public class JThread {
    private Stack<JFrame> frames = new Stack<>();
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
