package vjvm.interpreter;

import java.util.Arrays;

import lombok.Data;
import lombok.var;
import vjvm.interpreter.instruction.Decoder;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

@Data
public class Breakpoint {
  private final MethodInfo method;
  private final int offset;
  private final byte[] instruction;
  boolean enabled = false;

  public Breakpoint(MethodInfo method, int offset) {
    this.method = method;
    this.offset = offset;

    var code = method.code().code();
    var pc = new ProgramCounter(code);

    pc.position(offset);
    Decoder.decode(pc, method);
    instruction = Arrays.copyOfRange(method.code().code(), offset, pc.position());
  }
}
