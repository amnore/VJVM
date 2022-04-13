package vjvm.classloader.searchpath;

import lombok.SneakyThrows;
import lombok.var;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

public class ModuleSearchPath extends ClassSearchPath {
  private final Object[] modules;

  @SneakyThrows
  public ModuleSearchPath() {
    var finderClass = Class.forName("java.lang.module.ModuleFinder");
    var ofSystem = finderClass.getMethod("ofSystem");
    var findAll = finderClass.getMethod("findAll");
    var referenceClass = Class.forName("java.lang.module.ModuleReference");
    var open = referenceClass.getMethod("open");

    var finder = ofSystem.invoke(null);
    modules = ((Set<Object>) findAll.invoke(finder)).stream().map(m -> {
      try {
        return open.invoke(m);
      } catch (Exception e) {
        throw new Error(e);
      }
    }).toArray();
  }

  @Override
  @SneakyThrows
  public InputStream findClass(String name) {
    var readerClass = Class.forName("java.lang.module.ModuleReader");
    var open = readerClass.getMethod("open", String.class);

    for (var m : modules) {
      var s = (Optional<InputStream>) open.invoke(m, name + ".class");
      if (s.isPresent())
        return s.get();
    }

    return null;
  }

  @Override
  @SneakyThrows
  public void close() {
    for (var m : modules) {
      ((Closeable)m).close();
    }
  }
}
