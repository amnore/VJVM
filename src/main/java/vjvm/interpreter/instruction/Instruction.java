package vjvm.interpreter.instruction;

import vjvm.runtime.JThread;

/**
 * 指令基类
 */
public abstract class Instruction {

	/**
	 * 3 = opcode + operand (操作码1byte，操作数2byte)
	 */
	protected static final int INSTR_LENGTH = 3;

	/**
	 * 获得操作数并且执行该指令
	 * @param thread 当前线程
	 */
	public abstract void fetchAndRun(JThread thread);
}
