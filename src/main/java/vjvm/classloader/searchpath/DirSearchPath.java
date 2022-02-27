package vjvm.classloader.searchpath;

import lombok.var;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class DirSearchPath extends ClassSearchPath {
  private final Path searchPath;

  public DirSearchPath(String path) {
    searchPath = FileSystems.getDefault().getPath(path);
    assert !searchPath.toFile().exists() || searchPath.toFile().isDirectory();
  }

  @Override
  public InputStream findClass(String name) {
    try {
      return new FileInputStream(searchPath.resolve(name + ".class").toFile());
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  @Override
  public void close() {
    // there is nothing to close
  }

}
