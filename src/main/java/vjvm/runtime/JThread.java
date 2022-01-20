package vjvm.runtime;

import lombok.Getter;

import java.util.Stack;

public class JThread {
    private final Stack<JFrame> frames = new Stack<>();
    @Getter
    private ProgramCounter pc;
    @Getter
    private int exception;

    public JFrame currentFrame() {
        return frames.lastElement();
    }

    public void popFrame() {
        frames.pop();
        pc = frames.empty() ? null : frames.lastElement().pc();
    }

    public void pushFrame(JFrame frame) {
        frames.push(frame);
        pc = frame.pc();
    }

    public int frameCount() {
        return frames.size();
    }

    public boolean empty() {
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
