package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class GETSTATIC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        var stack = frame.opStack();
        FieldInfo field;
        JClass jClass;

        var ref = (FieldRef) frame.dynLink().constant(thread.pc().ushort());
        field = ref.info();
        jClass = ref.classRef().jClass();

        if (jClass.initState() != JClass.InitState.INITIALIZED)
            jClass.tryInitialize(thread);
        if (field.size() == 2)
            stack.pushLong(jClass.staticFields().long_(field.offset()));
        else
            stack.pushInt(jClass.staticFields().int_(field.offset()));
    }

}
