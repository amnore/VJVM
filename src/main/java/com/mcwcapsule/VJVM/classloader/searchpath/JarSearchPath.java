package com.mcwcapsule.VJVM.classloader.searchpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

public class JarSearchPath extends ClassSearchPath {
    // might be null if the file doesn't extsts.
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
            return file.getInputStream(file.getEntry(name));
        } catch (IOException e) {
            return null;
        }
    }
}
