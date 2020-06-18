package com.mcwcapsule.VJVM.classloader;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.mcwcapsule.VJVM.classloader.searchpath.ClassSearchPath;
import com.mcwcapsule.VJVM.runtime.JClass;

import lombok.val;
import lombok.var;

public class ClassLoader implements Closeable {
    // The parent of bootstrap class loader is null
    private ClassLoader parent;
    private ClassSearchPath[] searchPaths;
    private HashMap<String, JClass> definedClass;

    public ClassLoader(ClassLoader parent, String path) {
        this.parent = parent;
        searchPaths = ClassSearchPath.constructSearchPath(path);
    }

    /**
     * Defines a class given its binary representation, see spec 5.3.5
     * @param name name of the class
     * @param data data of the class
     * @return the defined class
     * @throws ClassNotFoundException sesolving super class and interfaces might throw this exception
     */
    public JClass defineClass(String name, InputStream data) throws ClassNotFoundException {
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
        // TODO: assign the class an index in method area
        return ret;
    }

    /**
     * Load a class of given name, or return it if the class was already loaded.
     * @param name name of the class to load
     * @return the loaded class
     * @throws ClassNotFoundException if the class was not found
     * I will not care about initiating loader because I am not verifying loading constraints.
     */
    public JClass loadClass(String name) throws ClassNotFoundException {
        JClass ret = null;
        // find in parent first
        try {
            if (parent != null)
                ret = parent.loadClass(name);
            return ret;
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
                return defineClass(name, iStream);
        }
        throw new ClassNotFoundException();
    }

    @Override
    public void close() throws IOException {
        for (val s : searchPaths)
            s.close();
    }
}
