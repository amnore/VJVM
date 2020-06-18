package com.mcwcapsule.VJVM.classloader.searchpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import lombok.val;

public class WildcardSearchPath extends ClassSearchPath {
    private JarFile[] jars;

    public WildcardSearchPath(String path) {
        assert path.endsWith("*");
        val searchPath = new File(path.substring(0, path.length() - 1));
        jars = Arrays.stream(searchPath.listFiles(fileName -> {
            val name = fileName.getName();
            return name.endsWith(".jar") || name.endsWith(".JAR");
        })).map(file -> {
            try {
                return new JarFile(file);
            } catch (IOException e) {
                throw new Error(e);
            }
        }).collect(Collectors.toList()).toArray(new JarFile[0]);
    }

    @Override
    public InputStream findClass(String name) {
        try {
            for (val jarFile : jars) {
                val entry = jarFile.getEntry(name + ".class");
                if (entry != null)
                    return jarFile.getInputStream(entry);
            }
        } catch (IOException e) {
            throw new Error(e);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        for (val file : jars)
            file.close();
    }

}
