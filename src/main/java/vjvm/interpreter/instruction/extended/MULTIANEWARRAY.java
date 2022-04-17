package vjvm.interpreter.instruction.extended;

import lombok.var;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;

public class MULTIANEWARRAY extends Instruction {
  private final JClass[] classes;
  private final int dimensions;

  public MULTIANEWARRAY(ProgramCounter pc, MethodInfo method) {
    var clazz = method.jClass();
    var cp = clazz.constantPool();
    var arrClass = (JClass) cp.constant(pc.ushort()).value();

    dimensions = pc.ubyte();
    assert dimensions >= 1;
    assert arrClass.name().lastIndexOf("[") >= dimensions - 1;

    classes = new JClass[dimensions + 1];
    classes[dimensions] = arrClass;
    for (int i = dimensions - 1; i >= 0; i--) {
      var desc = classes[i + 1].name().substring(1);
      if (!Descriptors.reference(desc))
        break;
      classes[i] = clazz.classLoader().loadClass(desc);
    }
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var lengths = new int[dimensions + 1];
    for (int i = 1; i <= dimensions; ++i)
      lengths[i] = stack.popInt();

    stack.pushAddress(createArray(lengths, dimensions));
  }

  @Override
  public String toString() {
    return String.format("multianewarray %s %d", classes[0].name(), dimensions);
  }

  private int createArray(int[] lengths, int dimension) {
    var arr = new ArrayObject(classes[dimension], lengths[dimension]);

    if (dimension != 1) {
      for (int i = 0; i < lengths[dimension]; i++) {
        arr.address(i, createArray(lengths, dimension - 1));
      }
    }

    return arr.address();
  }
}
