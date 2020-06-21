package com.mcwcapsule.VJVM.runtime;

import java.util.ArrayList;

public class JHeap {
    private ArrayList<JClass> methodArea = new ArrayList<>();
    private ArrayList<JObject> objects = new ArrayList<>();

    public int addJClass(JClass jClass) {
        methodArea.add(jClass);
        return methodArea.size() - 1;
    }

    public JClass getJClass(int index) {
        return methodArea.get(index);
    }

    public int addJObject(JObject object) {
        objects.add(object);
        return objects.size() - 1;
    }

    public JObject getJObject(int index) {
        return objects.get(index);
    }
}
