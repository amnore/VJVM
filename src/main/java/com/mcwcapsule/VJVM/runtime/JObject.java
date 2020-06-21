package com.mcwcapsule.VJVM.runtime;

import lombok.Getter;

@Getter
public class JObject {
    private JClass jClass;
    private Slots slots;

    private JObject(JClass jClass, int size) {
        this.jClass = jClass;
        slots = new Slots(size);
    }
}
