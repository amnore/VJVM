package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

public class NonArrayClass extends JClass {
    // construct from data
    public NonArrayClass(DataInput dataInput, JClassLoader classLoader) {
        try {
            // check magic number
            assert dataInput.readInt() == 0xCAFEBABE;
            // parse data
            // skip class version check
            minorVersion = dataInput.readShort();
            majorVersion = dataInput.readShort();

            constantPool = new RuntimeConstantPool(dataInput, this);
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
        this.classLoader = classLoader;
        String name = thisClass.getName();
        packageName = name.substring(0, name.lastIndexOf('/'));
        methodAreaIndex = VJVM.getHeap().addJClass(this);
    }

    public int createInstance() {
        assert getInitState() == InitState.INITIALIZED;
        val heap = VJVM.getHeap();
        int addr = heap.allocate(instanceSize);

        // set class index
        heap.getSlots().setInt(addr - 1, methodAreaIndex);
        return addr;
    }
}
