package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IFCOND extends Instruction {
    private final Condition cond;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

    private abstract static class Condition {
        abstract boolean accept(int value);
    }

    public static class EQ extends Condition {
        @Override
        boolean accept(int value) {
            return value == 0;
        }

    }

    public static class NE extends Condition {
        @Override
        boolean accept(int value) {
            return value != 0;
        }

    }

    public static class LT extends Condition {
        @Override
        boolean accept(int value) {
            return value < 0;
        }

    }

    public static class GE extends Condition {
        @Override
        boolean accept(int value) {
            return value >= 0;
        }

    }

    public static class GT extends Condition {
        @Override
        boolean accept(int value) {
            return value > 0;
        }

    }

    public static class LE extends Condition {
        @Override
        boolean accept(int value) {
            return value <= 0;
        }

    }

}
