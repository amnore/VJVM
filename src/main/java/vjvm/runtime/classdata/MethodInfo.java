package vjvm.runtime.classdata;

import lombok.var;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import vjvm.classfiledefs.MethodDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.attribute.Code;
import vjvm.runtime.classdata.constant.UTF8Constant;

import java.io.DataInput;

import static vjvm.classfiledefs.MethodAccessFlags.*;

public class MethodInfo {
  @Getter
  private final short accessFlags;
  @Getter
  private final String name;
  @Getter
  private final String descriptor;
  private final Attribute[] attributes;
  @Getter
  @Setter
  private JClass jClass;

  // if this method doesn't hava code attribute, then code is null.
  @Getter
  private Code code;

  @Getter
  @Setter
  private int vtableIndex = -1;

  @SneakyThrows
  public MethodInfo(DataInput dataInput, JClass jClass) {
    var constantPool = jClass.constantPool();

    this.jClass = jClass;
    accessFlags = dataInput.readShort();

    var nameIndex = dataInput.readUnsignedShort();
    name = ((UTF8Constant) constantPool.constant(nameIndex)).value();

    var descriptorIndex = dataInput.readUnsignedShort();
    descriptor = ((UTF8Constant) constantPool.constant(descriptorIndex)).value();

    var attrCount = dataInput.readUnsignedShort();
    attributes = new Attribute[attrCount];

    for (var i = 0; i < attrCount; ++i) {
      attributes[i] = Attribute.constructFromData(dataInput, constantPool);
    }

    for (var i : attributes) {
      if (i instanceof Code) {
        code = (Code) i;
        break;
      }
    }
  }

  public int argc() {
    return MethodDescriptors.argc(descriptor);
  }

  // spec. 5.4.4: method access control
  public boolean accessibleTo(JClass other, JClass referencedJClass) {
    if (public_())
      return true;

    if (protected_() && (other == jClass || other.subClassOf(jClass))) {
      if (static_())
        return true;
      if (referencedJClass == other || referencedJClass.subClassOf(other)
        || other.subClassOf(referencedJClass))
        return true;
    }

    if (protected_() || (!public_() && !private_())) {
      return jClass.runtimePackage().equals(other.runtimePackage());
    }

    if (private_()) {
      if (jClass == other)
        return true;

      return jClass.nestHost() == other.nestHost();
    }

    return false;
  }

  @Override
  public String toString() {
    return "MethodInfo{" + "name='" + name + '\'' +
      ", descriptor='" + descriptor + '\'' +
      ", vtableIndex=" + vtableIndex +
      '}';
  }

  public boolean public_() {
    return (accessFlags & ACC_PUBLIC) != 0;
  }

  public boolean private_() {
    return (accessFlags & ACC_PRIVATE) != 0;
  }

  public boolean protected_() {
    return (accessFlags & ACC_PROTECTED) != 0;
  }

  public boolean static_() {
    return (accessFlags & ACC_STATIC) != 0;
  }

  public boolean final_() {
    return (accessFlags & ACC_FINAL) != 0;
  }

  public boolean synchronized_() {
    return (accessFlags & ACC_SYNCHRONIZED) != 0;
  }

  public boolean bridge() {
    return (accessFlags & ACC_BRIDGE) != 0;
  }

  public boolean vaargs() {
    return (accessFlags & ACC_VARARGS) != 0;
  }

  public boolean native_() {
    return (accessFlags & ACC_NATIVE) != 0;
  }

  public boolean abstract_() {
    return (accessFlags & ACC_ABSTRACT) != 0;
  }

  public boolean strict() {
    return (accessFlags & ACC_STRICT) != 0;
  }

  public boolean synthetic() {
    return (accessFlags & ACC_SYNTHETIC) != 0;
  }
}
