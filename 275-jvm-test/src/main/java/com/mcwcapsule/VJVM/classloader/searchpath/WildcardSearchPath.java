package com.mcwcapsule.VJVM.classloader.searchpath;

import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.JarFile;

public class WildcardSearchPath extends ClassSearchPath {
    private final JarFile[] jars;

    public WildcardSearchPath(String path) {
        assert path.endsWith("*");
        val searchPath = new File(path.substring(0, path.length() - 1));
        assert searchPath.isDirectory();
        val files = searchPath.listFiles(fileName -> {
            val name = fileName.getName();
            return name.endsWith(".jar") || name.endsWith(".JAR");
        });
        jars = Arrays.stream(files).map(file -> {
            try {
                return new JarFile(file);
            } catch (IOException e) {
                throw new Error(e);
            }
        }).toArray(JarFile[]::new);
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
