package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.metadata.ClassAccessFlags;
import com.mcwcapsule.VJVM.runtime.metadata.FieldAccessFlags;
import com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.Constant;
import com.mcwcapsule.VJVM.vm.VJVM;

import lombok.val;

public class ArrayClass extends JClass {
    private String elementType;
    private ClassRef elementClass;
    private int elementSize;
    private FieldInfo lengthField;

    public ArrayClass(String arrayType, JClassLoader classLoader) {
        minorVersion = 0;
        majorVersion = 0;
        thisClass = new ClassRef(arrayType);
        superClass = new ClassRef("java/lang/Object");
        elementType = arrayType.substring(1, arrayType.length());
        elementSize = FieldDescriptors.getSize(elementType);
        this.classLoader = classLoader;

        // if element type is reference type, resolve it
        if (elementType.startsWith("L")) {
            elementClass = new ClassRef(elementType);
            try {
                elementClass.resolve(this);
            } catch (Exception e) {
                throw new Error(e);
            }
            constantPool = new RuntimeConstantPool(new Constant[] { thisClass, superClass, elementClass }, this);
        } else {
            constantPool = new RuntimeConstantPool(new Constant[] { thisClass, superClass }, this);
        }

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (FieldDescriptors.isReference(elementType)
                ? (elementClass.getJClass().getAccessFlags() & (ClassAccessFlags.ACC_PUBLIC))
                : ClassAccessFlags.ACC_PUBLIC) | ClassAccessFlags.ACC_SYNTHETIC);

        interfaces = new ClassRef[0];

        // length field
        fields = new FieldInfo[] { new FieldInfo(
                (short) (FieldAccessFlags.ACC_FINAL | FieldAccessFlags.ACC_PUBLIC | FieldAccessFlags.ACC_SYNTHETIC),
                "length", "I", new Attribute[0], this) };
        lengthField = fields[0];

        // there should be a clone() method, but I don't know how to generate it
        methods = new MethodInfo[0];

        attributes = new Attribute[0];
        String name = thisClass.getName();
        packageName = name.substring(0, name.lastIndexOf('/'));
        methodAreaIndex = VJVM.getHeap().addJClass(this);
    }

    public int createInstance(int length) {
        assert initState == InitState.INITIALIZED;
        val heap = VJVM.getHeap();
        val ret = heap.allocate(instanceSize + length * elementSize);

        // set array length
        heap.getSlots().setInt(ret + lengthField.getOffset(), length);
        return ret;
    }
}
