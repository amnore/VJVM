package vjvm.runtime.classdata.constant;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.FieldInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FieldRef extends ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;

    private FieldInfo info;
    private JClass jClass;

    /**
     * Resolves field reference. See spec. 5.4.3.2
     *
     * @param thisClass the class holding this reference
     */
    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        classRef.resolve(thisClass);
        jClass = classRef.jClass();
        info = jClass.findField(name, descriptor);
        if (info == null)
            throw new NoSuchFieldError();
        if (!info.accessibleTo(thisClass, jClass))
            throw new IllegalAccessError();
    }

    public int size() {
        return FieldDescriptors.size(descriptor);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldRef{");
        sb.append("name='").append(name).append('\'');
        sb.append(", descriptor='").append(descriptor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
