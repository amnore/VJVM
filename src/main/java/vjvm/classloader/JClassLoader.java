package vjvm.classloader;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.runtime.JClass;
import vjvm.runtime.JClass.InitState;
import vjvm.runtime.JThread;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VMContext;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import lombok.Getter;
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
        var ret = ArrayUtil.createArrayClass(name, this);

        definedClass.put(name, ret);
        ret.prepare();
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
        // fix for class name in the form of Lclass;
        if (name.charAt(0) == 'L' && name.charAt(name.length() - 1) == ';')
            name = name.substring(1, name.length() - 1);

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
    public void close() throws IOException {
        for (var s : searchPaths)
            s.close();
    }
}
