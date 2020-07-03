package com.mcwcapsule.VJVM.utils;

import java.io.File;
import java.util.Arrays;

public class FileUtil {
    /**
     * Deletes a single file, or deletes a dir recursively.
     *
     * @param file the file to delete
     *             if file is null or file does not exit, then do nothing.
     * @return whether the file have been deleted, returns false if the file is null or does not exit.
     */
    public static boolean DeleteRecursive(File file) {
        if (file == null || !file.exists())
            return false;
        if (file.isDirectory())
            Arrays.stream(file.listFiles()).forEach(FileUtil::DeleteRecursive);
        return file.delete();
    }
}
