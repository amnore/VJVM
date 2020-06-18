package com.mcwcapsule.VJVM.runtime;

import java.io.DataInput;
import java.io.IOException;

import com.mcwcapsule.VJVM.classloader.ClassLoader;
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
    private ClassLoader loader;

    public JClass(DataInput dataInput, ClassLoader initLoader) {
        try {
            this.loader = initLoader;
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
}
