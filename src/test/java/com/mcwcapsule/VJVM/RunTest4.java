package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RunTest4 {
    @Test
    void test() {
        val path = CompileUtil.compile("Test8.java", "TestUtil.java");
        VJVM.init(VMOptions.builder().userClassPath(path.toString()).entryClass("testsource/Test8").build());
    }
}
