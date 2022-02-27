package vjvm.runtime.classdata.constant;

import lombok.var;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;
import vjvm.runtime.JClass;
import vjvm.runtime.object.StringObject;

import java.io.DataInput;

public class StringConstant extends Constant {
  private final int stringIndex;
  private final JClass thisClass;
  private UTF8Constant utf8;
  private int address = 0;

  @SneakyThrows
  StringConstant(DataInput input, JClass thisClass) {
    stringIndex = input.readUnsignedShort();
    this.thisClass = thisClass;
  }

  private UTF8Constant utf8() {
    if (utf8 == null) {
      utf8 = (UTF8Constant) thisClass.constantPool().constant(stringIndex);
    }
    return utf8;
  }

  @Override
  public Integer value() {
    if (address == 0) {
      var s = utf8();
      var ctx = thisClass.context();
      address = ctx.heap().intern(new StringObject(s.value(), ctx));
    }
    return address;
  }

  @Override
  public String toString() {
    return String.format("String: \"%s\"", StringEscapeUtils.escapeJava(utf8().value()));
  }
}
