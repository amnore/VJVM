package vjvm.runtime.object;

import lombok.Getter;
import vjvm.runtime.JClass;

public class ClassObject extends JObject {
  @Getter
  private final JClass jClass;

  public ClassObject(JClass jClass) {
    super(jClass.name().equals("java/lang/Class")
      ? jClass
      : jClass.context().bootstrapLoader().loadClass("Ljava/lang/Class;"));
    this.jClass = jClass;
  }
}
