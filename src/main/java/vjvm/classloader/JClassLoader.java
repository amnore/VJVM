package vjvm.classloader;

import vjvm.classfiledefs.ClassAccessFlags;
import vjvm.classfiledefs.FieldAccessFlags;
import vjvm.classfiledefs.Descriptors;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.vm.VMContext;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import lombok.SneakyThrows;
import vjvm.vm.VMGlobalObject;

import static vjvm.classfiledefs.ClassAccessFlags.ACC_FINAL;
import static vjvm.classfiledefs.ClassAccessFlags.ACC_PUBLIC;

public class JClassLoader extends VMGlobalObject implements Closeable {
    // The parent of bootstrap class loader is null
    private final JClassLoader parent;
    private final ClassSearchPath[] searchPaths;
    private final HashMap<String, JClass> definedClass = new HashMap<>();

    private static final HashSet<String> primitiveClasses = new HashSet<>();

    public JClassLoader(JClassLoader parent, ClassSearchPath[] searchPaths, VMContext context) {
        super(context);
        this.parent = parent;
        this.searchPaths = searchPaths;
    }

    /**
     * Load a class of given name, or return it if the class was already loaded.
     * I will not care about initiating loader because I am not verifying loading constraints.
     *
     * @param descriptor name of the class to load
     * @return the loaded class
     */
    public JClass loadClass(String descriptor) {
        // array class
        if (descriptor.charAt(0) == Descriptors.DESC_array) {
            if (definedClass.get(descriptor) != null)
                return definedClass.get(descriptor);
            if (Descriptors.reference(descriptor.charAt(1))) {
                var elemClass = loadClass(descriptor.substring(1));
                return elemClass.classLoader().defineArrayClass(descriptor);
            } else return context().bootstrapLoader().defineArrayClass(descriptor);
        }

        // primitive class
        if (descriptor.charAt(0) != Descriptors.DESC_reference) {
            return context().bootstrapLoader().definePrimitiveClass(descriptor);
        }

        // ordinary class
        assert descriptor.charAt(descriptor.length()-1) == ';';

        // find in parent first
        JClass jClass;
        if (parent != null
            && (jClass = parent.loadClass(descriptor)) != null) {
            return jClass;
        }

        // find in loaded classes
        if ((jClass = definedClass.get(descriptor)) != null)
            return jClass;

        // not loaded
        var name = descriptor.substring(1, descriptor.length()-1);
        for (var p : searchPaths) {
            var iStream = p.findClass(name);
            // if the class was found
            if (iStream != null)
                return defineNonarrayClass(descriptor, iStream);
        }

        return null;
    }

    @Override
    @SneakyThrows
    public void close() {
        for (var s : searchPaths)
            s.close();
    }
    /**
     * Defines a nonarray class, see spec 5.3.5
     *
     * @param descriptor type descriptor, e.g. Ljava/lang/String;
     * @param data data of the class
     * @return the defined class
     */
    private JClass defineNonarrayClass(String descriptor, InputStream data) {
        var ret = new JClass(new DataInputStream(data), this);

        // add to loaded classes
        definedClass.put(descriptor, ret);
        return ret;
    }

    private JClass defineArrayClass(String descriptor) {
        var componentType = descriptor.substring(1);
        JClass componentClass = loadClass(componentType);

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        var accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (Descriptors.reference(componentType)
            ? (componentClass.accessFlags() & (ClassAccessFlags.ACC_PUBLIC))
            : ClassAccessFlags.ACC_PUBLIC) | ClassAccessFlags.ACC_SYNTHETIC);

        // length field
        var fields = new FieldInfo[]{new FieldInfo(
            (short) (FieldAccessFlags.ACC_FINAL | FieldAccessFlags.ACC_PUBLIC | FieldAccessFlags.ACC_SYNTHETIC),
            "length", "I", new Attribute[0])};
        var lengthField = fields[0];

        // there should be a clone() method, but I don't know how to generate it
        // TODO: implement clone() as native method
        var methods = new MethodInfo[0];

        var ret = new JClass(
            this,
            accessFlags,
            descriptor,
            "java/lang/Object",
            // Arrays implement Cloneable and Serializable, see JLS 4.10.3.
            new String[]{"java/lang/Cloneable", "java/io/Serializable"},
            fields,
            methods
        );

        ret.prepare();
        definedClass.put(descriptor, ret);
        return ret;
    }

    private JClass definePrimitiveClass(String descriptor) {
        if (!primitiveClasses.contains(descriptor))
            return null;

        return definedClass.computeIfAbsent(descriptor, desc ->
            new JClass(
                this,
                (short) (ACC_FINAL|ACC_PUBLIC),
                desc,
                null,
                new String[0],
                new FieldInfo[0],
                new MethodInfo[0]
            )
        );
    }

    static {
        primitiveClasses.add("Z");
        primitiveClasses.add("B");
        primitiveClasses.add("C");
        primitiveClasses.add("D");
        primitiveClasses.add("F");
        primitiveClasses.add("I");
        primitiveClasses.add("J");
        primitiveClasses.add("S");
        primitiveClasses.add("V");
    }
}
