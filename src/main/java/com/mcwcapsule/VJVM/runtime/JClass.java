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

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;

import lombok.Getter;

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
                fields[i] = new FieldInfo(dataInput, constantPool);
            int methodsCount = dataInput.readUnsignedShort();
            methods = new MethodInfo[methodsCount];
            for (int i = 0; i < methodsCount; ++i)
                methods[i] = new MethodInfo(dataInput, constantPool);
            int attributesCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attributesCount];
            for (int i = 0; i < attributesCount; ++i)
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }

    public ClassRef getSuperInterface(int index) {
        return interfaces[index];
    }

    public int getSuperInterfacesCount() {
        return interfaces.length;
    }

    public boolean isAccessibleTo(JClass other) {
        return isPublic() || classLoader.equals(other.classLoader);
    }

    public boolean isPublic() {
        return accessFlags == ACC_PUBLIC;
    }

    public boolean isFinal() {
        return accessFlags == ACC_FINAL;
    }

    public boolean isSuper() {
        return accessFlags == ACC_SUPER;
    }

    public boolean isInterface() {
        return accessFlags == ACC_INTERFACE;
    }

    public boolean isAbstract() {
        return accessFlags == ACC_ABSTRACT;
    }

    public boolean isSynthetic() {
        return accessFlags == ACC_SYNTHETIC;
    }

    public boolean isAnnotation() {
        return accessFlags == ACC_ANNOTATION;
    }

    public boolean isEnum() {
        return accessFlags == ACC_ENUM;
    }

    public boolean isModule() {
        return accessFlags == ACC_MODULE;
    }
}
