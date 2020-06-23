package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;
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
        jClass = classRef.getJClass();
        info = jClass.findField(name, descriptor);
        if (info == null)
            throw new NoSuchFieldError();
        if (!info.isAccessibleTo(thisClass, jClass))
            throw new IllegalAccessError();
    }

    public int getSize() {
        return FieldDescriptors.getSize(descriptor);
    }
}
