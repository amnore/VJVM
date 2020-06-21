package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IF_ACMPCOND extends Instruction {
    private final Comparison cmp;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

    private abstract static class Comparison {
        public abstract boolean accept(int left, int right);
    }

    public static class EQ extends Comparison {
        @Override
        public boolean accept(int left, int right) {
            return left == right;
        }
    }

    public static class NE extends Comparison {
        @Override
        public boolean accept(int left, int right) {
            return left != right;
        }
    }
}
