package vjvm.runtime.classdata;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.constant.UTF8Constant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.DataInput;
import java.io.IOException;

import static vjvm.classfiledefs.FieldAccessFlags.*;

@RequiredArgsConstructor
public class FieldInfo {
    private final short accessFlags;
    @Getter
    private final String name;
    @Getter
    private final String descriptor;
    private final Attribute[] attributes;
    @Setter
    private JClass jClass;

    // Offset of this field in slots
    @Getter
    @Setter
    private int offset;

    public FieldInfo(DataInput dataInput, JClass jClass) {
        try {
            this.jClass = jClass;
            var constantPool = jClass.constantPool();
            accessFlags = dataInput.readShort();
            int nameIndex = dataInput.readUnsignedShort();
            name = ((UTF8Constant) constantPool.constant(nameIndex)).value();
            int descIndex = dataInput.readUnsignedShort();
            descriptor = ((UTF8Constant) constantPool.constant(descIndex)).value();
            int attributesCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attributesCount];
            for (int i = 0; i < attributesCount; ++i)
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }

    /**
     * Check whether this field is accessible to another class. See spec. 5.4.4
     *
     * @param other            the referencing class
     * @param referencedJClass the referenced class in FieldRef
     * @return whether this field is accessible to other
     */
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
        return "FieldInfo{" + "name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            ", offset=" + offset +
            '}';
    }

    public int attributeCount() {
        return attributes.length;
    }

    public Attribute attribute(int index) {
        return attributes[index];
    }

    public int size() {
        return FieldDescriptors.size(descriptor);
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

    public boolean transient_() {
        return (accessFlags & ACC_TRANSIENT) != 0;
    }

    public boolean synthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }

    public boolean enum_() {
        return (accessFlags & ACC_ENUM) != 0;
    }
}
