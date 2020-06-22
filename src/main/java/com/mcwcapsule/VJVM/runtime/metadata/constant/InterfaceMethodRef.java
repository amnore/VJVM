package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InterfaceMethodRef extends Constant implements ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private MethodInfo info;

    /**
     * Resolves the referenced interface method. See spec. 5.4.3.4
     */
    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        classRef.resolve(thisClass);
        if (!classRef.getJClass().isInterface())
            throw new IncompatibleClassChangeError();
        // Use JDK7 rules, same as MethodRef.
        info = classRef.getJClass().findMethod(name, descriptor);
        if (info == null)
            throw new NoSuchMethodError();
        if (!info.isAccessibleTo(thisClass, classRef.getJClass()))
            throw new IllegalAccessError();
    }
}
