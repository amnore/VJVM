package vjvm.runtime.classdata.attribute;

import lombok.SneakyThrows;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.ClassRef;

import java.io.DataInput;

public class NestMember extends Attribute {
	ClassRef[] members;

	@SneakyThrows
	NestMember(DataInput input, ConstantPool constantPool) {
		var num = input.readUnsignedShort();
		members = new ClassRef[num];
		for (var i = 0; i < num; i++) {
			members[i] = (ClassRef) constantPool.constant(input.readUnsignedShort());
		}
	}

	public ClassRef member(int index) {
		return members[index];
	}

	public boolean contains(String name) {
		for (ClassRef member : members) {
			if (member.name().equals(name)) {
				return true;
			}
		}

		return false;
	}
}
