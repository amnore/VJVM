package vjvm.runtime.object;

import lombok.Getter;
import vjvm.runtime.JClass;

public class ClassObject extends JObject {
	@Getter
	private final JClass jClass;

	public ClassObject(JClass jClass) {
		super("java/lang/Class".equals(jClass.name())
			? jClass
			: jClass.context().bootstrapLoader().loadClass("Ljava/lang/Class;"));
		this.jClass = jClass;
	}
}
