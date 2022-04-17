package vjvm.interpreter.instruction.math;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

public class IINC extends Instruction {
  private final int index;
  private final byte value;

  public IINC(ProgramCounter pc, MethodInfo method) {
    index = pc.ubyte();
    value = pc.byte_();
  }

  @Override
  public void run(JThread thread) {
    var slots = thread.top().vars();
    slots.int_(index, slots.int_(index) + value);
  }

  @Override
  public String toString() {
    return String.format("iinc %d, %d", index, value);
  }
}
