package vjvm.interpreter.instruction.extended;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.ArrayUtil;
import vjvm.vm.VJVM;

public class MULTIANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var arrClassRef = (ClassRef) frame.dynLink().constant(thread.pc().ushort());
        var dimensions = thread.pc().ubyte();
        assert dimensions >= 1;
        assert arrClassRef.name().lastIndexOf("[") >= dimensions - 1;
        var stack = frame.opStack();
        var dimArr = new int[dimensions + 1];
        for (int i = 1; i <= dimensions; ++i)
            dimArr[i] = stack.popInt();
        var arrClasses = new JClass[dimensions + 1];
        try {
            arrClassRef.resolve(frame.jClass());
            arrClasses[dimensions] = arrClassRef.jClass();
            for (int i = dimensions - 1; i >= 0; --i) {
                var name = arrClasses[i + 1].thisClass().name().substring(1);
                if (!FieldDescriptors.reference(name))
                    break;
                // if the component type is primitive type
                if (!FieldDescriptors.reference(name))
                    arrClasses[i] = JClass.primitiveClass(name);
                else
                    arrClasses[i] = frame.jClass().classLoader().loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        stack.pushAddress(createArrayRecursive(dimArr, arrClasses, dimensions));
    }

    private int createArrayRecursive(int[] dimensionArr, JClass[] arrClasses, int current) {
        assert current > 0;
        var slots = VJVM.heap().slots();
        var arr = ArrayUtil.newInstance(arrClasses[current], dimensionArr[current]);
        if (current != 1)
            for (int i = 0; i < dimensionArr[current]; ++i)
                slots.addressAt(arr + arrClasses[current].instanceSize() + i,
                    createArrayRecursive(dimensionArr, arrClasses, current - 1));
        return arr;
    }
}
