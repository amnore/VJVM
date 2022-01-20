package vjvm.runtime;

import lombok.Getter;

import java.util.Stack;

public class JThread {
    private final Stack<JFrame> frames = new Stack<>();
    @Getter
    private ProgramCounter PC;
    @Getter
    private int exception;

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

    public int getFrameCount() {
        return frames.size();
    }

    public boolean isEmpty() {
        return frames.empty();
    }

    public boolean hasException() {
        return exception != 0;
    }

    public void clearException() {
        assert hasException();
        exception = 0;
    }

    /**
     * Throw an exception at specified thread.
     *
     * @param exception reference of the exception object to throw
     */
    public void throwException(int exception) {
        assert !hasException();
        this.exception = exception;
    }

}
