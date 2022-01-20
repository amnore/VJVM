package vjvm.runtime.classdata;

import vjvm.classfiledefs.MethodDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.attribute.Code;
import vjvm.runtime.classdata.constant.UTF8Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

import static vjvm.classfiledefs.MethodAccessFlags.*;

public class MethodInfo {
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

    public MethodInfo(DataInput dataInput, JClass jClass) {
        try {
            this.jClass = jClass;
            val constantPool = jClass.getConstantPool();
            accessFlags = dataInput.readShort();
            int nameIndex = dataInput.readUnsignedShort();
            name = ((UTF8Constant) constantPool.getConstant(nameIndex)).getValue();
            int descriptorIndex = dataInput.readUnsignedShort();
            descriptor = ((UTF8Constant) constantPool.getConstant(descriptorIndex)).getValue();
            int attrCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attrCount];
            for (int i = 0; i < attrCount; ++i) {
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
            }
        } catch (IOException e) {
            throw new ClassFormatError();
        }
        for (val i : attributes)
            if (i instanceof Code) {
                code = (Code) i;
                break;
            }
    }

    public int getArgc() {
        return MethodDescriptors.getArgc(descriptor);
    }

    public boolean isAccessibleTo(JClass other, JClass referencedJClass) {
        if (isPublic())
            return true;
        if (isProtected() && (other == jClass || other.isSubClassOf(jClass))) {
            if (isStatic())
                return true;
            if (referencedJClass == other || referencedJClass.isSubClassOf(other)
                || other.isSubClassOf(referencedJClass))
                return true;
        }
        if (isProtected() || (!isPublic() && !isPrivate())) {
            return jClass.getRuntimePackage().equals(other.getRuntimePackage());
        }
        return isPrivate() && other == jClass;
    }

    @Override
    public String toString() {
        return "MethodInfo{" + "name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            ", vtableIndex=" + vtableIndex +
            '}';
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean isPrivate() {
        return (accessFlags & ACC_PRIVATE) != 0;
    }

    public boolean isProtected() {
        return (accessFlags & ACC_PROTECTED) != 0;
    }

    public boolean isStatic() {
        return (accessFlags & ACC_STATIC) != 0;
    }

    public boolean isFinal() {
        return (accessFlags & ACC_FINAL) != 0;
    }

    public boolean isSynchronized() {
        return (accessFlags & ACC_SYNCHRONIZED) != 0;
    }

    public boolean isBridge() {
        return (accessFlags & ACC_BRIDGE) != 0;
    }

    public boolean isVaargs() {
        return (accessFlags & ACC_VARARGS) != 0;
    }

    public boolean isNative() {
        return (accessFlags & ACC_NATIVE) != 0;
    }

    public boolean isAbstract() {
        return (accessFlags & ACC_ABSTRACT) != 0;
    }

    public boolean isStrict() {
        return (accessFlags & ACC_STRICT) != 0;
    }

    public boolean isSynthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }
}
