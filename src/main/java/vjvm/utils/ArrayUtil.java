package vjvm.utils;

import vjvm.classfiledefs.ClassAccessFlags;
import vjvm.classfiledefs.FieldAccessFlags;
import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.JClassLoader;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.vm.VJVM;
import lombok.val;

import static vjvm.classfiledefs.FieldDescriptors.*;

public class ArrayUtil {
    public static String getComponentType(String arrayType) {
        assert arrayType.charAt(0) == '[';
        return arrayType.substring(1);
    }

    public static JClass getComponentClass(JClass arrayClass) {
        assert arrayClass.isArray();

        val componentType = getComponentType(arrayClass.getName());
        // for primitive types
        if (!FieldDescriptors.isReference(componentType))
            return JClass.getPrimitiveClass(componentType);
        try {
            return arrayClass.getClassLoader().loadClass(componentType);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    public static int getLength(int arrayRef) {
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(arrayRef - 1));
        assert jClass.isArray();
        return slots.getInt(arrayRef + jClass.getInstanceSize() - 1);
    }

    public static int newInstance(JClass arrayClass, int length) {
        // allocate array
        val objSize = arrayClass.getInstanceSize();
        val arrSize = getLengthInSlots(arrayClass.getName().substring(1), length);
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val ret = heap.allocate(objSize + arrSize);

        // set the length and classIndex of new array
        slots.setInt(ret - 1, arrayClass.getMethodAreaIndex());
        slots.setInt(ret + objSize - 1, length);

        return ret;
    }

    public static JClass createArrayClass(String arrayType, JClassLoader classLoader) {
        short minorVersion = 0;
        short majorVersion = 0;
        val thisClass = new ClassRef(arrayType);
        val superClass = new ClassRef("java/lang/Object");
        val componentType = arrayType.substring(1);

        JClass componentClass;
        try {
            // if the component is of primitive type
            if (!FieldDescriptors.isReference(componentType))
                componentClass = JClass.getPrimitiveClass(componentType);
            else
                componentClass = classLoader.loadClass(componentType);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        val accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (FieldDescriptors.isReference(componentType)
            ? (componentClass.getAccessFlags() & (ClassAccessFlags.ACC_PUBLIC))
            : ClassAccessFlags.ACC_PUBLIC) | ClassAccessFlags.ACC_SYNTHETIC);

        // Arrays implement Cloneable and Serializable, see JLS 4.10.3.
        val interfaces = new ClassRef[]
            {new ClassRef("java/lang/Cloneable"), new ClassRef("java/io/Serializable")};

        // length field
        val fields = new FieldInfo[]{new FieldInfo(
            (short) (FieldAccessFlags.ACC_FINAL | FieldAccessFlags.ACC_PUBLIC | FieldAccessFlags.ACC_SYNTHETIC),
            "length", "I", new Attribute[0])};
        val lengthField = fields[0];

        // there should be a clone() method, but I don't know how to generate it
        val methods = new MethodInfo[0];

        val attributes = new Attribute[0];

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

    public static int getLengthInSlots(String componentType, int length) {
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

    public static char getChar(int array, int index) {
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(array - 1));
        assert jClass.isArray();
        val raw = slots.getRaw();
        return raw.getChar((array + jClass.getInstanceSize()) * 4 + index * 2);
    }

    public static void setChar(int array, int index, char value) {
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(array - 1));
        assert jClass.isArray();
        val raw = slots.getRaw();
        raw.putChar((array + jClass.getInstanceSize()) * 4 + index * 2, value);
    }
}
