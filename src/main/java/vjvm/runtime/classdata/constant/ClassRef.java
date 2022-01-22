package vjvm.runtime.classdata.constant;

import vjvm.runtime.JClass;
import lombok.Getter;

@Getter
public class ClassRef extends ResolvableConstant {
    private final String name;
    private JClass jClass;

    public ClassRef(JClass thisClass, String name) {
        super(thisClass);
        this.name = name;
    }

    public JClass jClass() {
        if (jClass == null) {
            resolve();
        }

        return jClass;
    }

    @Override
    public void resolve() {
        // check whether the reference points to this class
        if (name.equals(thisClass().thisClass().name))
            jClass = thisClass();
            // if not, load the Class using the defining class loader of this class
        else
            jClass = thisClass().classLoader().loadClass(name);
        // check accessibility
        // workaround for arrays
        if (thisClass().array())
            return;
        if (!jClass.accessibleTo(thisClass()))
            throw new IllegalAccessError();
    }

    @Override
    public String toString() {
        return "ClassRef{" + "name='" + name + '\'' +
            '}';
    }
}
