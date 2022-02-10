package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;

import java.io.DataInput;

public class FieldRef extends Constant {
  private final int classIndex;
  private final int nameAndTypeIndex;
  private final JClass self;

  private ClassRef classRef;
  private NameAndTypeConstant nameAndType;
  private FieldInfo field;

  @SneakyThrows
  FieldRef(DataInput input, JClass self) {
    classIndex = input.readUnsignedShort();
    nameAndTypeIndex = input.readUnsignedShort();
    this.self = self;
  }

  private ClassRef classRef() {
    if (classRef == null) {
      classRef = (ClassRef) self.constantPool().constant(classIndex);
    }
    return classRef;
  }

  private NameAndTypeConstant nameAndType() {
    if (nameAndType == null) {
      nameAndType = (NameAndTypeConstant) self.constantPool().constant(nameAndTypeIndex);
    }
    return nameAndType;
  }

  @Override
  public FieldInfo value() {
    if (field != null) {
      return field;
    }

        /*
          Resolves field reference. See spec. 5.4.3.2
         */
    var ref = classRef().value();
    var pair = nameAndType().value();

    field = ref.findField(pair.getLeft(), pair.getRight());
    if (field == null)
      throw new NoSuchFieldError();
    if (!field.accessibleTo(self, ref))
      throw new IllegalAccessError();

    return field;
  }

  @Override
  public String toString() {
    return String.format("Fieldref: %s.%s:%s", classRef().name(), nameAndType().name(), nameAndType().type());
  }
}
