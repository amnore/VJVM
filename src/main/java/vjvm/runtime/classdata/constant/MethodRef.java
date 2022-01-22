package vjvm.runtime.classdata.constant;

import vjvm.classfiledefs.MethodDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.MethodInfo;
import lombok.Getter;

@Getter
public class MethodRef extends ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private final boolean interfaceMethod;

    private MethodInfo info;
    private JClass jClass;

    public MethodRef(ClassRef classRef, String name, String descriptor, boolean interfaceMethod, JClass thisClass) {
        super(thisClass);
        this.classRef = classRef;
        this.name = name;
        this.descriptor = descriptor;
        this.interfaceMethod = interfaceMethod;
    }

    public  MethodInfo info() {
        if (info == null) {
            resolve();
        }
        return info;
    }

    public  JClass jClass() {
        if (jClass == null) {
            resolve();
        }
        return jClass;
    }

    /**
     * Resolve the referenced method. See spec. 5.4.3.3, 5.4.3.4.
     */
    @Override
    public void resolve() {
        classRef.resolve();
        jClass = classRef.jClass();
        if (jClass.interface_() ^ interfaceMethod)
            throw new IncompatibleClassChangeError();
        // ignore signature polymorphic methods
        info = jClass.findMethod(name, descriptor);
        if (info == null)
            throw new NoSuchMethodError();
        if (!info.accessibleTo(thisClass(), jClass))
            throw new IllegalAccessError();
    }

    public int argc() {
        return MethodDescriptors.argc(descriptor);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodRef{");
        sb.append("name='").append(name).append('\'');
        sb.append(", descriptor='").append(descriptor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
