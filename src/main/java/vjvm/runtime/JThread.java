package vjvm.runtime;

import lombok.var;
import lombok.Getter;
import lombok.Setter;
import vjvm.runtime.object.JObject;
import vjvm.vm.VMContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JThread {
  private final ArrayList<JFrame> frames = new ArrayList<>();
  @Getter
  @Setter
  private JObject exception;
  @Getter
  private final VMContext context;

  public JThread(VMContext context) {
    this.context = context;
  }

  public JFrame top() {
    return empty() ? null : frames.get(frames.size() - 1);
  }

  public void pop() {
    frames.remove(frames.size() - 1);
  }

  public void push(JFrame frame) {
    frames.add(frame);
  }

  public ProgramCounter pc() {
    return empty() ? null : top().pc();
  }

  public boolean empty() {
    return frames.isEmpty();
  }

  public List<JFrame> frames() {
    return Collections.unmodifiableList(frames);
  }
}
