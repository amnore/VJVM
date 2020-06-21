package com.mcwcapsule.VJVM.runtime.metadata;

import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_ENUM;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_FINAL;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_PRIVATE;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_PROTECTED;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_PUBLIC;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_STATIC;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_SYNTHETIC;
import static com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags.ACC_TRANSIENT;

import java.io.DataInput;
import java.io.IOException;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.constant.FieldRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UTF8Constant;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class FieldInfo {
    private final short accessFlags;
    @Getter
    private final String name;
    @Getter
    private final String descriptor;
    private final Attribute[] attributes;
    private final JClass jClass;

    // Offset of this field in slots
    @Getter
    @Setter
    private int offset;

    public FieldInfo(DataInput dataInput, JClass jClass) {
        try {
            this.jClass = jClass;
            val constantPool = jClass.getConstantPool();
            accessFlags = dataInput.readShort();
            int nameIndex = dataInput.readUnsignedShort();
            name = ((UTF8Constant) constantPool.getConstant(nameIndex)).getValue();
            int descIndex = dataInput.readUnsignedShort();
            descriptor = ((UTF8Constant) constantPool.getConstant(descIndex)).getValue();
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
     * @param other the referencing class
     * @param referencedJClass the referenced class in FieldRef
     * @return whether this field is accessible to other
     */
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
        if (isPrivate() && other == jClass)
            return true;
        return false;
    }

    public int getAttributeCount() {
        return attributes.length;
    }

    public Attribute getAttribute(int index) {
        return attributes[index];
    }

    public int getSize() {
        return FieldDescriptors.getSize(descriptor);
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

    public boolean isTransient() {
        return (accessFlags & ACC_TRANSIENT) != 0;
    }

    public boolean isSynthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }

    public boolean isEnum() {
        return (accessFlags & ACC_ENUM) != 0;
    }
}
