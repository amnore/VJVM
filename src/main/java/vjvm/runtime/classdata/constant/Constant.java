package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import vjvm.runtime.JClass;

import java.io.DataInput;

import static vjvm.classfiledefs.ConstantTags.*;

public abstract class Constant {
  @SneakyThrows
  public static Pair<Constant, Integer> construntFromData(DataInput input, JClass jClass) {
    var tag = input.readByte();
    var count = (tag == CONSTANT_Long || tag == CONSTANT_Double) ? 2 : 1;

    var result = switch (tag) {
      case CONSTANT_Class -> new ClassRef(input, jClass);
      case CONSTANT_Fieldref -> new FieldRef(input, jClass);
      case CONSTANT_Methodref -> new MethodRef(input, jClass, false);
      case CONSTANT_InterfaceMethodref -> new MethodRef(input, jClass, true);
      case CONSTANT_String -> new StringConstant(input, jClass);
      case CONSTANT_Integer -> new IntegerConstant(input);
      case CONSTANT_Float -> new FloatConstant(input);
      case CONSTANT_Long -> new LongConstant(input);
      case CONSTANT_Double -> new DoubleConstant(input);
      case CONSTANT_NameAndType -> new NameAndTypeConstant(input, jClass);
      case CONSTANT_Utf8 -> new UTF8Constant(input);
      case CONSTANT_MethodHandle -> new UnknownConstant(input, 3);
      case CONSTANT_MethodType -> new UnknownConstant(input, 2);
      case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> new UnknownConstant(input, 4);
      default -> throw new ClassFormatError();
    };

    return Pair.of(result, count);
  }

  public abstract Object value();
}
