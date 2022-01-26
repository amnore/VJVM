package vjvm.runtime.classdata.constant;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;
import lombok.Getter;

@Getter
public class FieldRef extends ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;

    private FieldInfo info;
    private JClass jClass;

    public FieldRef(JClass thisClass, ClassRef classRef, String name, String descriptor) {
        super(thisClass);
        this.classRef = classRef;
        this.name = name;
        this.descriptor = descriptor;
    }

    public FieldInfo info() {
        if (info == null) {
            resolve();
        }
        return info;
    }

    public JClass jClass() {
        if (jClass == null) {
            resolve();
        }
        return jClass;
    }

    /**
     * Resolves field reference. See spec. 5.4.3.2
     */
    @Override
    public void resolve() {
        classRef.resolve();
        jClass = classRef.jClass();
        info = jClass.findField(name, descriptor);
        if (info == null)
            throw new NoSuchFieldError();
        if (!info.accessibleTo(thisClass(), jClass))
            throw new IllegalAccessError();
    }

    public int size() {
        return FieldDescriptors.size(descriptor);
    }

    @Override
    public String toString() {
        return "FieldRef{" + "name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            '}';
    }
}
