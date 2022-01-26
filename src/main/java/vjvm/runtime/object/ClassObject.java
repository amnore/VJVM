package vjvm.runtime.object;

import lombok.Getter;
import vjvm.runtime.JClass;

public class ClassObject extends JObject {
    @Getter
    private final JClass jClass;

    public ClassObject(JClass jClass) {
        super(jClass.context().bootstrapLoader().loadClass("java/lang/Class"));
        this.jClass = jClass;
    }
}
