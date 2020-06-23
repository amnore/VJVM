package com.mcwcapsule.VJVM.runtime.metadata;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Code;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UTF8Constant;
import lombok.Getter;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

import static com.mcwcapsule.VJVM.runtime.metadata.MethodAccessFlags.*;

public class MethodInfo {
    private final short accessFlags;
    @Getter
    private final String name;
    @Getter
    private final String descriptor;
    private final Attribute[] attributes;
    private final JClass jClass;

    // if this method doesn't hava code attribute, then code is null.
    @Getter
    private Code code;

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
        if (isProtected() && (other == jClass || other.isSubclassOf(jClass))) {
            if (isStatic())
                return true;
            if (referencedJClass == other || referencedJClass.isSubclassOf(other)
                || other.isSubclassOf(referencedJClass))
                return true;
        }
        if (isProtected() || (!isPublic() && !isPrivate())) {
            return jClass.getRuntimePackage().equals(other.getRuntimePackage());
        }
        return isPrivate() && other == jClass;
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
