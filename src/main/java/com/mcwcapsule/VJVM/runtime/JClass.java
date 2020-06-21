package com.mcwcapsule.VJVM.runtime;

import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_ABSTRACT;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_ANNOTATION;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_ENUM;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_FINAL;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_INTERFACE;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_MODULE;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_PUBLIC;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_SUPER;
import static com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags.ACC_SYNTHETIC;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.ConstantValue;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;

import lombok.Getter;
import lombok.val;

import static com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors.*;

public class JClass {
    @Getter
    private short minorVersion;
    @Getter
    private short majorVersion;
    @Getter
    private RuntimeConstantPool constantPool;
    @Getter
    private short accessFlags;
    @Getter
    private ClassRef thisClass;
    @Getter
    private ClassRef superClass;
    private ClassRef[] interfaces;
    private FieldInfo[] fields;
    private MethodInfo[] methods;
    private Attribute[] attributes;

    @Getter
    private JClassLoader classLoader;

    @Getter
    private Slots staticFields;

    public JClass(DataInput dataInput, JClassLoader initLoader) {
        try {
            this.classLoader = initLoader;
            // check magic number
            assert dataInput.readInt() == 0xCAFEBABE;
            // parse data
            // skip class version check
            minorVersion = dataInput.readShort();
            majorVersion = dataInput.readShort();

            constantPool = new RuntimeConstantPool(dataInput);
            accessFlags = dataInput.readShort();
            int thisIndex = dataInput.readUnsignedShort();
            thisClass = (ClassRef) constantPool.getConstant(thisIndex);
            int superIndex = dataInput.readUnsignedShort();
            if (superIndex != 0)
                superClass = (ClassRef) constantPool.getConstant(superIndex);
            int interfacesCount = dataInput.readUnsignedShort();
            interfaces = new ClassRef[interfacesCount];
            for (int i = 0; i < interfacesCount; ++i) {
                int interfaceIndex = dataInput.readUnsignedShort();
                interfaces[i] = (ClassRef) constantPool.getConstant(interfaceIndex);
            }
            int fieldsCount = dataInput.readUnsignedShort();
            fields = new FieldInfo[fieldsCount];
            for (int i = 0; i < fieldsCount; ++i)
                fields[i] = new FieldInfo(dataInput, this);
            int methodsCount = dataInput.readUnsignedShort();
            methods = new MethodInfo[methodsCount];
            for (int i = 0; i < methodsCount; ++i)
                methods[i] = new MethodInfo(dataInput, this);
            int attributesCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attributesCount];
            for (int i = 0; i < attributesCount; ++i)
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }

    public void verify() {
        // not verifying
    }

    /**
     * Prepares this class for use. See spec. 5.4.2
     */
    public void prepare() {
        // create static fields
        int slotSize = 0;
        val staticFieldInfos = Arrays.stream(fields).filter(s -> s.isStatic()).collect(Collectors.toList());
        for (val field : staticFieldInfos) {
            field.setOffset(slotSize);
            slotSize += FieldDescriptors.getSize(field.getDescriptor());
        }
        staticFields = new Slots(slotSize);
        // load ConstantValue
        for (val field : staticFieldInfos) {
            if (!field.isFinal())
                continue;
            for (int i = 0; i < field.getAttributeCount(); ++i)
                if (field.getAttribute(i) instanceof ConstantValue) {
                    int offset = field.getOffset();
                    Object value = ((ConstantValue) field.getAttribute(i)).getValue();
                    switch (field.getDescriptor().charAt(0)) {
                        case DESC_boolean:
                        case DESC_byte:
                        case DESC_char:
                        case DESC_short:
                        case DESC_int:
                            // The stored value is an Integer
                            staticFields.setInt(offset, (Integer) value);
                            break;
                        case DESC_float:
                            staticFields.setFloat(offset, (Float) value);
                            break;
                        case DESC_long:
                            staticFields.setLong(offset, (Long) value);
                            break;
                        case DESC_double:
                            staticFields.setDouble(offset, (Double) value);
                            break;
                    }
                    break;
                }
        }
        // TODO: also prepare instance fields
    }

    /**
     * Finds a field recursively. This is used to resolve field references. See spec. 5.4.3.2
     * @param name name of the field to find
     * @param descriptor descriptor of the field to find
     * @return the found field, or null if not found
     */
    public FieldInfo findField(String name, String descriptor) {
        for (val field : fields)
            if (field.getName().equals(name) && field.getDescriptor().equals(descriptor))
                return field;
        // find in super interfaces
        for (val si : interfaces) {
            // according to spec. 5.3.5.3, the reference to super interfaces have already been resolved.
            val result = si.getJClass().findField(name, descriptor);
            if (result != null)
                return result;
        }
        // then find in super class
        return superClass == null ? null : superClass.getJClass().findField(name, descriptor);
    }

    /**
     * Similar to findField, but find in super classes first. See spec. 5.4.3.3
     */
    public MethodInfo findMethod(String name, String descriptor) {
        for (val method : methods)
            if (method.getName().equals(name) && method.getDescriptor().equals(descriptor))
                return method;
        if (superClass != null) {
            val result = superClass.getJClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        // Using the rules from JDK7 instead of JDK8
        for (val si : interfaces) {
            val result = si.getJClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        return null;
    }

    public ClassRef getSuperInterface(int index) {
        return interfaces[index];
    }

    public int getSuperInterfacesCount() {
        return interfaces.length;
    }

    /**
     * Checks whether this class is a subclass of another class. Super interfaces are not taken into account.
     * @param other the class to check against.
     * @return Whether this class is a subclass of other. Returns false if this == other.
     */
    public boolean isSubclassOf(JClass other) {
        return superClass.getJClass() == other ? true
                : superClass == null ? false : superClass.getJClass().isSubclassOf(other);
    }

    public boolean isAccessibleTo(JClass other) {
        // FIXME: check runtime package
        return isPublic() || classLoader.equals(other.classLoader);
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean isFinal() {
        return (accessFlags & ACC_FINAL) != 0;
    }

    public boolean isSuper() {
        return (accessFlags & ACC_SUPER) != 0;
    }

    public boolean isInterface() {
        return (accessFlags & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (accessFlags & ACC_ABSTRACT) != 0;
    }

    public boolean isSynthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }

    public boolean isAnnotation() {
        return (accessFlags & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (accessFlags & ACC_ENUM) != 0;
    }

    public boolean isModule() {
        return (accessFlags & ACC_MODULE) != 0;
    }
}
