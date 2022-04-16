package vjvm.runtime;

import lombok.var;
import lombok.Getter;
import lombok.Setter;
import vjvm.runtime.object.JObject;
import vjvm.vm.VMContext;

import java.util.Stack;

public class JThread {
  private final Stack<JFrame> frames = new Stack<>();
  @Getter
  @Setter
  private JObject exception;
  @Getter
  private final VMContext context;

  public JThread(VMContext context) {
    this.context = context;
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
