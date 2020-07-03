package com.mcwcapsule.VJVM.interpreter.instruction.extended;

import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;

public class MULTINEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val arrClassRef = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        val dimensions = thread.getPC().getUnsignedByte();
        assert dimensions >= 1;
        assert arrClassRef.getName().lastIndexOf("[") >= dimensions - 1;
        val stack = frame.getOpStack();
        val dimArr = new int[dimensions + 1];
        for (int i = 1; i <= dimensions; ++i)
            dimArr[i] = stack.popInt();
        val arrClasses = new JClass[dimensions + 1];
        try {
            arrClassRef.resolve(frame.getJClass());
            arrClasses[dimensions] = arrClassRef.getJClass();
            for (int i = dimensions - 1; i >= 0; --i) {
                var name = arrClasses[i + 1].getThisClass().getName().substring(1);
                if (!FieldDescriptors.isReference(name)) break;
                if (name.startsWith("L"))
                    name = name.substring(1, name.length() - 1);
                arrClasses[i] = frame.getJClass().getClassLoader().loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        stack.pushAddress(createArrayRecursive(dimArr, arrClasses, dimensions));
    }

    private int createArrayRecursive(int[] dimensionArr, JClass[] arrClasses, int current) {
        val slots = VJVM.getHeap().getSlots();
        val arr = ((ArrayClass) arrClasses[current]).createInstance(dimensionArr[current]);
        if (current != 1)
            for (int i = 0; i < dimensionArr[current]; ++i)
                slots.setAddress(
                    arr + arrClasses[current].getInstanceSize() + i,
                    createArrayRecursive(dimensionArr, arrClasses, current - 1));
        return arr;
    }
}
