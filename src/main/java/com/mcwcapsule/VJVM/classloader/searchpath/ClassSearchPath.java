package com.mcwcapsule.VJVM.classloader.searchpath;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.val;

/**
 * Represents a path to search class files in.
 */
public abstract class ClassSearchPath {
    /**
     * Finds a class with specified name. 
     * @param name name of the class to find.
     * @return Returns a stream containing the binary data if such class is found, or null if not.
     */
    public abstract InputStream findClass(String name);

    /**
     * Constructs search paths object with given path.
     */
    public static ClassSearchPath[] constructSearchPath(String path) {
        String sep = System.getProperty("path.separator");
        return (ClassSearchPath[]) Arrays.stream(path.split(sep)).map(searchPath -> {
            if (searchPath.endsWith(".jar") || searchPath.endsWith(".JAR"))
                return new JarSearchPath(searchPath);
            if (searchPath.endsWith("*"))
                return new WildcardSearchPath(searchPath);
            return new DirSearchPath(searchPath);
        }).collect(Collectors.toList()).toArray(new ClassSearchPath[0]);
    }
}
