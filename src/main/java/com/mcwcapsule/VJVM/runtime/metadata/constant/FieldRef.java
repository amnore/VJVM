package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.FieldInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FieldRef extends Constant implements ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private FieldInfo info;

    /**
     * Resolves field reference. See spec. 5.4.3.2
     * @param thisClass the class holding this reference
     */
    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        classRef.resolve(thisClass);
        info = classRef.getJClass().findField(name, descriptor);
        if (info == null)
            throw new NoSuchFieldError();
        if (!info.isAccessibleTo(thisClass, classRef.getJClass()))
            throw new IllegalAccessError();
    }
}
