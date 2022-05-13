package vjvm.interpreter;

import java.util.Arrays;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.var;
import vjvm.classfiledefs.Opcodes;
import vjvm.interpreter.instruction.Decoder;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

@Getter
public class Breakpoint {
  private final MethodInfo method;
  private final int offset;
  private final byte[] instruction;
  private boolean enabled = false;

  public Breakpoint(MethodInfo method, int offset) {
    this.method = method;
    this.offset = offset;

    var code = method.code().code();
    var pc = new ProgramCounter(code);

    pc.position(offset);
    Decoder.decode(pc, method);
    instruction = Arrays.copyOfRange(method.code().code(), offset, pc.position());
  }

  public void enable() {
    var code = method.code().code();
    Arrays.fill(code, offset, offset + instruction.length, Opcodes.OPC_breakpoint);
    enabled = true;
  }

  public void disable() {
    var code = method.code().code();
    var instr = instruction;
    System.arraycopy(instr, 0, code, offset, instr.length);
    enabled = false;
  }
}
