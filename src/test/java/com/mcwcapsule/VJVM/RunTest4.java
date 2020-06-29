package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RunTest4 {
    @Test
    void test() {
        val path = CompileUtil.compile("Test8.java", "TestUtil.java");
        VJVM.init(VMOptions.builder().entryClass("testsource/Test8").userClassPath(path.toString()).build());
        FileUtil.DeleteRecursive(path.toFile());
    }
}
