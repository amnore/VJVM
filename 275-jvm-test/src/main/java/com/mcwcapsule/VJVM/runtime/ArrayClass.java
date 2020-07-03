package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.classfiledefs.ClassAccessFlags;
import com.mcwcapsule.VJVM.classfiledefs.FieldAccessFlags;
import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.classdata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.classdata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.classdata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.classdata.constant.Constant;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.Getter;
import lombok.val;

import static com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors.*;

public class ArrayClass extends JClass {
    @Getter
    private final String elementType;
    private final FieldInfo lengthField;
    @Getter
    private ClassRef elementClass;

    public ArrayClass(String arrayType, JClassLoader classLoader) {
        minorVersion = 0;
        majorVersion = 0;
        thisClass = new ClassRef(arrayType);
        superClass = new ClassRef("java/lang/Object");
        elementType = arrayType.substring(1);
        this.classLoader = classLoader;

        // if element type is reference type, resolve it
        if (elementType.startsWith("L")) {
            elementClass = new ClassRef(elementType);
            try {
                elementClass.resolve(this);
            } catch (Exception e) {
                throw new Error(e);
            }
            constantPool = new RuntimeConstantPool(new Constant[]{thisClass, superClass, elementClass}, this);
        } else {
            constantPool = new RuntimeConstantPool(new Constant[]{thisClass, superClass}, this);
        }

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (FieldDescriptors.isReference(elementType)
            ? (elementClass.getJClass().getAccessFlags() & (ClassAccessFlags.ACC_PUBLIC))
            : ClassAccessFlags.ACC_PUBLIC) | ClassAccessFlags.ACC_SYNTHETIC);

        // Arrays do implement some interfaces, see JLS 4.10.3, but it's not considered here.
        interfaces = new ClassRef[0];

        // length field
        fields = new FieldInfo[]{new FieldInfo(
            (short) (FieldAccessFlags.ACC_FINAL | FieldAccessFlags.ACC_PUBLIC | FieldAccessFlags.ACC_SYNTHETIC),
            "length", "I", new Attribute[0], this)};
        lengthField = fields[0];

        // there should be a clone() method, but I don't know how to generate it
        methods = new MethodInfo[0];

        attributes = new Attribute[0];
        String name = thisClass.getName();

        // array types doesn't have a package
        packageName = null;

        methodAreaIndex = VJVM.getHeap().addJClass(this);
    }

    public int createInstance(int length) {
        assert initState == InitState.INITIALIZED;
        val heap = VJVM.getHeap();
        val arrSize = getArraySize(length);
        val ret = heap.allocate(instanceSize + getArraySize(length));
        val slots = heap.getSlots();

        // set class index
        slots.setInt(ret - 1, methodAreaIndex);
        // set array length
        heap.getSlots().setInt(ret + lengthField.getOffset(), length);
        return ret;
    }

    private int getArraySize(int length) {
        switch (elementType.charAt(0)) {
            case DESC_boolean:
            case DESC_byte:
                length += (-length & 0b11);
                return length / 4;
            case DESC_char:
            case DESC_short:
                length += length & 1;
                return length / 2;
            case DESC_double:
            case DESC_long:
                return length * 2;
            default:
                return length;
        }
    }
}
