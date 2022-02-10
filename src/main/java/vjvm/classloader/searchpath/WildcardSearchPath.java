package vjvm.classloader.searchpath;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.JarFile;

public class WildcardSearchPath extends ClassSearchPath {
  private final JarFile[] jars;

  public WildcardSearchPath(String path) {
    assert path.endsWith("*");

    var searchPath = new File(path.substring(0, path.length() - 1));
    assert searchPath.isDirectory();

    var files = searchPath.listFiles(fileName -> {
      var name = fileName.getName();
      return name.endsWith(".jar") || name.endsWith(".JAR");
    });

    jars = files == null ? new JarFile[0]
      : Arrays.stream(files).map(file -> {
      try {
        return new JarFile(file);
      } catch (IOException e) {
        throw new Error(e);
      }
    }).toArray(JarFile[]::new);
  }

  @Override
  @SneakyThrows
  public InputStream findClass(String name) {
    for (var jarFile : jars) {
      var entry = jarFile.getEntry(name + ".class");
      if (entry != null)
        return jarFile.getInputStream(entry);
    }

    return null;
  }

  @Override
  @SneakyThrows
  public void close() {
    for (var file : jars)
      file.close();
  }

}
