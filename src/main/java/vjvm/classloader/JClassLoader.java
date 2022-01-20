package vjvm.classloader;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.runtime.JClass;
import vjvm.runtime.JClass.InitState;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VJVM;
import lombok.val;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class JClassLoader implements Closeable {
    // The parent of bootstrap class loader is null
    private final JClassLoader parent;
    private final ClassSearchPath[] searchPaths;
    private final HashMap<String, JClass> definedClass = new HashMap<>();

    public JClassLoader(JClassLoader parent, String path) {
        this.parent = parent;
        searchPaths = ClassSearchPath.constructSearchPath(path);
    }

    /**
     * Defines a nonarray class, see spec 5.3.5
     *
     * @param name name of the class
     * @param data data of the class
     * @return the defined class
     * @throws ClassNotFoundException sesolving super class and interfaces might throw this exception
     */
    private JClass defineNonarrayClass(String name, InputStream data) throws ClassNotFoundException {
        val ret = new JClass(new DataInputStream(data), this);
        // check the name of created class matches what we expect, see spec 5.3.5.2
        // resolve ClassRef first
        ret.getThisClass().resolve(ret);
        if (!ret.getThisClass().getName().equals(name))
            throw new NoClassDefFoundError();
        // resolve the super class and super interfaces, see spec 5.3.5 and 5.4.1
        if (ret.getSuperClass() != null)
            ret.getSuperClass().resolve(ret);
        for (int i = 0; i < ret.getSuperInterfacesCount(); ++i)
            ret.getSuperInterface(i).resolve(ret);
        // add to loaded class
        definedClass.put(name, ret);
        ret.setInitState(InitState.LOADED);
        return ret;
    }

    private JClass defineArrayClass(String name) {
        val ret = ArrayUtil.createArrayClass(name, this);
        try {
            ret.getThisClass().resolve(ret);
            ret.getSuperClass().resolve(ret);
            for (int i = 0; i < ret.getSuperInterfacesCount(); ++i)
                ret.getSuperInterface(i).resolve(ret);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        definedClass.put(name, ret);

        // arrays doesn't need init
        ret.tryPrepare();
        ret.setInitState(InitState.INITIALIZED);
        return ret;
    }

    /**
     * Load a class of given name, or return it if the class was already loaded.
     * I will not care about initiating loader because I am not verifying loading constraints.
     *
     * @param name name of the class to load
     * @return the loaded class
     * @throws ClassNotFoundException if the class was not found
     */
    public JClass loadClass(String name) throws ClassNotFoundException {
        // fix for class name in the form of Lclass;
        if (name.charAt(0) == 'L' && name.charAt(name.length() - 1) == ';')
            name = name.substring(1, name.length() - 1);

        // if the class is an array class
        if (name.charAt(0) == FieldDescriptors.DESC_array) {
            if (definedClass.get(name) != null)
                return definedClass.get(name);
            if (FieldDescriptors.isReference(name.charAt(1))) {
                val elemClass = loadClass(name.substring(1));
                return elemClass.getClassLoader().defineArrayClass(name);
            } else return VJVM.getBootstrapLoader().defineArrayClass(name);
        }

        // the class is not an array class
        // find in parent first
        JClass ret;
        try {
            if (parent != null) {
                ret = parent.loadClass(name);
                return ret;
            }
        } catch (ClassNotFoundException e) {
            // the parent didn't find the class
        }
        // find in loaded classes
        ret = definedClass.get(name);
        if (ret != null)
            return ret;
        // not loaded
        for (var p : searchPaths) {
            var iStream = p.findClass(name);
            // if the class was found
            if (iStream != null)
                return defineNonarrayClass(name, iStream);
        }
        throw new ClassNotFoundException(name);
    }

    public JClass getDefinedClass(String name) {
        return definedClass.get(name);
    }

    @Override
    public void close() throws IOException {
        for (val s : searchPaths)
            s.close();
    }
}
