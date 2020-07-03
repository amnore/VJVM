package com.mcwcapsule.VJVM.classloader;

import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.classloader.searchpath.ClassSearchPath;
import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JClass.InitState;
import com.mcwcapsule.VJVM.runtime.NonArrayClass;
import lombok.val;
import lombok.var;

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
        val ret = new NonArrayClass(new DataInputStream(data), this);
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
        val ret = new ArrayClass(name, this);
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
     *
     * @param name name of the class to load
     * @return the loaded class
     * @throws ClassNotFoundException if the class was not found
     *                                I will not care about initiating loader because I am not verifying loading constraints.
     */
    public JClass loadClass(String name) throws ClassNotFoundException {
        // if the class is an array class
        if (name.charAt(0) == FieldDescriptors.DESC_array) {
            if (name.charAt(1) == FieldDescriptors.DESC_reference) {
                val elemClass = loadClass(name.substring(2, name.length() - 1));
                return elemClass.getClassLoader().defineArrayClass(name);
            } else return defineArrayClass(name);
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