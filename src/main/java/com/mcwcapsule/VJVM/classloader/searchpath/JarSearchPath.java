package com.mcwcapsule.VJVM.classloader.searchpath;

import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

public class JarSearchPath extends ClassSearchPath {
    // might be null if the file doesn't exist.
    private JarFile file;

    public JarSearchPath(String name) {
        try {
            file = new JarFile(name);
        } catch (IOException e) {
            file = null;
        }
    }

    @Override
    public InputStream findClass(String name) {
        if (file == null)
            return null;
        try {
            val entry = file.getEntry(name + ".class");
            return entry == null ? null : file.getInputStream(entry);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        if (file != null)
            file.close();
    }

}
