package vjvm.runtime.classdata.constant;

import lombok.var;
import java.io.DataInput;
import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.MethodInfo;

public class MethodRef extends Constant {
  private final int classIndex;
  private final int nameAndTypeIndex;
  private final JClass self;
  private final boolean interface_;

  private ClassRef classRef;
  private NameAndTypeConstant nameAndType;
  private MethodInfo method;

  @SneakyThrows
  MethodRef(DataInput input, JClass self, boolean interface_) {
    classIndex = input.readUnsignedShort();
    nameAndTypeIndex = input.readUnsignedShort();
    this.self = self;
    this.interface_ = interface_;
  }

  public JClass jClass() { return classRef().value(); }

  private ClassRef classRef() {
    if (classRef == null) {
      classRef = (ClassRef)self.constantPool().constant(classIndex);
    }
    return classRef;
  }

  private NameAndTypeConstant nameAndType() {
    if (nameAndType == null) {
      nameAndType =
          (NameAndTypeConstant)self.constantPool().constant(nameAndTypeIndex);
    }
    return nameAndType;
  }

  /**
   * Resolve the referenced method. See spec. 5.4.3.3, 5.4.3.4.
   */
  @Override
  public MethodInfo value() {
    if (method != null) {
      return method;
    }

    var pair = nameAndType().value();

    if (jClass().interface_() ^ interface_)
      throw new IncompatibleClassChangeError();

    // ignore signature polymorphic methods
    method = jClass().findMethod(pair.getLeft(), pair.getRight(), false);
    if (method == null) {
      throw new NoSuchMethodError();
    }
    if (!method.accessibleTo(self, jClass())) {
      throw new IllegalAccessError();
    }

    return method;
  }

  @Override
  public String toString() {
    return String.format(
        "%s: %s.%s:%s", interface_ ? "InterfaceMethodref" : "Methodref",
        classRef().name(), nameAndType().name(), nameAndType().type());
  }
}
