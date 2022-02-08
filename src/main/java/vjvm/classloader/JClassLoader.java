package vjvm.classloader;

import vjvm.classfiledefs.ClassAccessFlags;
import vjvm.classfiledefs.FieldAccessFlags;
import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.vm.VMContext;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import lombok.SneakyThrows;
import vjvm.vm.VMGlobalObject;

public class JClassLoader extends VMGlobalObject implements Closeable {
    // The parent of bootstrap class loader is null
    private final JClassLoader parent;
    private final ClassSearchPath[] searchPaths;
    private final HashMap<String, JClass> definedClass = new HashMap<>();

    public JClassLoader(JClassLoader parent, ClassSearchPath[] searchPaths, VMContext context) {
        super(context);
        this.parent = parent;
        this.searchPaths = searchPaths;
    }

    /**
     * Defines a nonarray class, see spec 5.3.5
     *
     * @param name name of the class
     * @param data data of the class
     * @return the defined class
     */
    private JClass defineNonarrayClass(String name, InputStream data) {
        var ret = new JClass(new DataInputStream(data), this);

        // add to loaded classes
        definedClass.put(name, ret);
        return ret;
    }

    private JClass defineArrayClass(String name) {
        var componentType = name.substring(1);

        JClass componentClass;
        // if the component is of primitive type
        if (!FieldDescriptors.reference(componentType))
            componentClass = context().primitiveClass(componentType);
        else
            componentClass = loadClass(componentType);

        // for reference types, the accessibility of array class is the same as element type, see spec. 5.3.3.2
        var accessFlags = (short) (ClassAccessFlags.ACC_FINAL | (FieldDescriptors.reference(componentType)
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
            name,
            "java/lang/Object",
            // Arrays implement Cloneable and Serializable, see JLS 4.10.3.
            new String[]{"java/lang/Cloneable", "java/io/Serializable"},
            fields,
            methods
        );

        ret.prepare();
        definedClass.put(name, ret);
        return ret;
    }

    /**
     * Load a class of given name, or return it if the class was already loaded.
     * I will not care about initiating loader because I am not verifying loading constraints.
     *
     * @param name name of the class to load
     * @return the loaded class
     */
    public JClass loadClass(String name) {
        // normalize class name
        if (name.charAt(0) == 'L' && name.charAt(name.length() - 1) == ';')
            name = name.substring(1, name.length() - 1);
        name = name.replace('.', '/');

        // if the class is an array class
        if (name.charAt(0) == FieldDescriptors.DESC_array) {
            if (definedClass.get(name) != null)
                return definedClass.get(name);
            if (FieldDescriptors.reference(name.charAt(1))) {
                var elemClass = loadClass(name.substring(1));
                return elemClass.classLoader().defineArrayClass(name);
            } else return context().bootstrapLoader().defineArrayClass(name);
        }

        // the class is not an array class
        // find in parent first
        JClass jClass;
        if (parent != null
            && (jClass = parent.loadClass(name)) != null) {
            return jClass;
        }

        // find in loaded classes
        if ((jClass = definedClass.get(name)) != null)
            return jClass;

        // not loaded
        for (var p : searchPaths) {
            var iStream = p.findClass(name);
            // if the class was found
            if (iStream != null)
                return defineNonarrayClass(name, iStream);
        }

        return null;
    }

    public JClass definedClass(String name) {
        return definedClass.get(name);
    }

    @Override
    @SneakyThrows
    public void close() {
        for (var s : searchPaths)
            s.close();
    }
}
