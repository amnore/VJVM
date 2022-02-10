package vjvm.classloader.searchpath;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;

public class ModuleSearchPath extends ClassSearchPath {
  private final ModuleReader[] modules;

  public ModuleSearchPath(ModuleFinder finder) {
    modules = finder.findAll().stream().map(m -> {
      try {
        return m.open();
      } catch (IOException e) {
        throw new Error(e);
      }
    }).toArray(ModuleReader[]::new);
  }

  @Override
  @SneakyThrows
  public InputStream findClass(String name) {
    for (var m : modules) {
      var s = m.open(name + ".class");
      if (s.isPresent())
        return s.get();
    }

    return null;
  }

  @Override
  @SneakyThrows
  public void close() {
    for (var m : modules) {
      m.close();
    }
  }
}
