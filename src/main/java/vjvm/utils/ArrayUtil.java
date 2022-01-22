package vjvm.utils;

import vjvm.classfiledefs.ClassAccessFlags;
import vjvm.classfiledefs.FieldAccessFlags;
import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.JClassLoader;
import vjvm.runtime.JClass;
import vjvm.runtime.JHeap;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.constant.ClassRef;

import static vjvm.classfiledefs.FieldDescriptors.*;

public class ArrayUtil {
    public static String componentType(String arrayType) {
        assert arrayType.charAt(0) == '[';
        return arrayType.substring(1);
    }

    public static JClass componentClass(JClass arrayClass) {
        assert arrayClass.array();

        var componentType = componentType(arrayClass.name());
        // for primitive types
        if (!FieldDescriptors.reference(componentType))
            return JClass.primitiveClass(componentType);
        return arrayClass.classLoader().loadClass(componentType);
    }

    public static int length(int arrayRef, JHeap heap) {
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(arrayRef - 1));
        assert jClass.array();
        return slots.int_(arrayRef + jClass.instanceSize() - 1);
    }

    public static int newInstance(JClass arrayClass, int length, JHeap heap) {
        // allocate array
        var objSize = arrayClass.instanceSize();
        var arrSize = lengthInSlots(arrayClass.name().substring(1), length);
        var slots = heap.slots();
        var ret = heap.allocate(objSize + arrSize);

        // set the length and classIndex of new array
        slots.int_(ret - 1, arrayClass.methodAreaIndex());
        slots.int_(ret + objSize - 1, length);

        return ret;
    }

    public static JClass createArrayClass(String arrayType, JClassLoader classLoader) {
        short minorVersion = 0;
        short majorVersion = 0;
        var thisClass = new ClassRef(arrayType);
        var superClass = new ClassRef("java/lang/Object");
        var componentType = arrayType.substring(1);

        JClass componentClass;
        // if the component is of primitive type
        if (!FieldDescriptors.reference(componentType))
            componentClass = JClass.primitiveClass(componentType);
        else
            componentClass = classLoader.loadClass(componentType);

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        var accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (FieldDescriptors.reference(componentType)
            ? (componentClass.accessFlags() & (ClassAccessFlags.ACC_PUBLIC))
            : ClassAccessFlags.ACC_PUBLIC) | ClassAccessFlags.ACC_SYNTHETIC);

        // Arrays implement Cloneable and Serializable, see JLS 4.10.3.
        var interfaces = new ClassRef[]
            {new ClassRef("java/lang/Cloneable"), new ClassRef("java/io/Serializable")};

        // length field
        var fields = new FieldInfo[]{new FieldInfo(
            (short) (FieldAccessFlags.ACC_FINAL | FieldAccessFlags.ACC_PUBLIC | FieldAccessFlags.ACC_SYNTHETIC),
            "length", "I", new Attribute[0])};
        var lengthField = fields[0];

        // there should be a clone() method, but I don't know how to generate it
        var methods = new MethodInfo[0];

        var attributes = new Attribute[0];

        return new JClass(
            classLoader,
            minorVersion,
            majorVersion,
            null,
            accessFlags,
            thisClass,
            superClass,
            interfaces,
            fields,
            methods,
            attributes
        );
    }

    public static int lengthInSlots(String componentType, int length) {
        switch (componentType.charAt(0)) {
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

    public static char getChar(int array, int index, JHeap heap) {
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(array - 1));
        assert jClass.array();
        return slots.charAt((array + jClass.instanceSize()) * 4 + index * 2);
    }

    public static void setChar(int array, int index, char value, JHeap heap) {
        var slots = heap.slots();
        var jClass = heap.jClass(slots.int_(array - 1));
        assert jClass.array();
        slots.charAt((array + jClass.instanceSize()) * 4 + index * 2, value);
    }
}
