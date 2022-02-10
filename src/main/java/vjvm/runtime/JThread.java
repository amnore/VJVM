package vjvm.runtime;

import lombok.Getter;
import lombok.Setter;
import vjvm.runtime.object.JObject;
import vjvm.vm.VMContext;
import vjvm.vm.VMGlobalObject;

import java.util.Stack;

public class JThread extends VMGlobalObject {
  private final Stack<JFrame> frames = new Stack<>();
  @Getter
  @Setter
  private JObject exception;

  public JThread(VMContext context) {
    super(context);
  }

  public JFrame top() {
    return empty() ? null : frames.lastElement();
  }

  public void pop() {
    frames.pop();
  }

  public void push(JFrame frame) {
    frames.push(frame);
  }

  public ProgramCounter pc() {
    return empty() ? null : top().pc();
  }

  public boolean empty() {
    return frames.empty();
  }
}
