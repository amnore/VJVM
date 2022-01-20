package vjvm.interpreter.instruction.extended;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VJVM;
import lombok.val;

public class MULTIANEWARRAY extends Instruction {

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
                if (!FieldDescriptors.isReference(name))
                    break;
                // if the component type is primitive type
                if (!FieldDescriptors.isReference(name))
                    arrClasses[i] = JClass.getPrimitiveClass(name);
                else
                    arrClasses[i] = frame.getJClass().getClassLoader().loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        stack.pushAddress(createArrayRecursive(dimArr, arrClasses, dimensions));
    }

    private int createArrayRecursive(int[] dimensionArr, JClass[] arrClasses, int current) {
        assert current > 0;
        val slots = VJVM.getHeap().getSlots();
        val arr = ArrayUtil.newInstance(arrClasses[current], dimensionArr[current]);
        if (current != 1)
            for (int i = 0; i < dimensionArr[current]; ++i)
                slots.setAddress(arr + arrClasses[current].getInstanceSize() + i,
                    createArrayRecursive(dimensionArr, arrClasses, current - 1));
        return arr;
    }
}
