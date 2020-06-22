package com.mcwcapsule.VJVM.runtime.metadata;

import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UTF8Constant;
import lombok.Getter;

import java.io.DataInput;
import java.io.IOException;

import static com.mcwcapsule.VJVM.runtime.metadata.MethodAccessFlags.*;

public class InterfaceMethodInfo {
    private final short accessFlags;
    @Getter
    private final String name;
    @Getter
    private final String descriptor;
    private final Attribute[] attributes;

    public InterfaceMethodInfo(DataInput dataInput, RuntimeConstantPool constantPool) {
        try {
            accessFlags = dataInput.readShort();
            int nameIndex = dataInput.readUnsignedShort();
            name = ((UTF8Constant) constantPool.getConstant(nameIndex)).getValue();
            int descriptorIndex = dataInput.readUnsignedShort();
            descriptor = ((UTF8Constant) constantPool.getConstant(descriptorIndex)).getValue();
            int attrCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attrCount];
            for (int i = 0; i < attrCount; ++i)
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
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
