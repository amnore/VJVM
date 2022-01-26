package vjvm.interpreter.instruction.extended;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.ArrayObject;
import vjvm.vm.VMContext;

public class MULTIANEWARRAY extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        var arrClass = ((ClassRef) frame.link().constant(thread.pc().ushort())).value();
        var dimensions = thread.pc().ubyte();
        assert dimensions >= 1;
        assert arrClass.name().lastIndexOf("[") >= dimensions - 1;
        var stack = frame.stack();
        var dimArr = new int[dimensions + 1];
        for (int i = 1; i <= dimensions; ++i)
            dimArr[i] = stack.popInt();

        var arrClasses = new JClass[dimensions + 1];
        arrClasses[dimensions] = arrClass;
        for (int i = dimensions - 1; i >= 0; --i) {
            var name = arrClasses[i + 1].name().substring(1);
            if (!FieldDescriptors.reference(name))
                break;
            // if the component type is primitive type
            if (!FieldDescriptors.reference(name))
                arrClasses[i] = thread.context().primitiveClass(name);
            else
                arrClasses[i] = frame.jClass().classLoader().loadClass(name);
        }

        stack.pushAddress(createArrayRecursive(dimArr, arrClasses, dimensions, thread.context()));
    }

    private int createArrayRecursive(int[] dimensionArr, JClass[] arrClasses, int current, VMContext ctx) {
        assert current > 0;
        var arr = new ArrayObject(arrClasses[current], dimensionArr[current]);

        if (current != 1) {
            for (int i=0;i<dimensionArr[current]; i++) {
                arr.address(i, createArrayRecursive(dimensionArr, arrClasses, current - 1, ctx));
            }
        }

        return arr.address();
    }
}
