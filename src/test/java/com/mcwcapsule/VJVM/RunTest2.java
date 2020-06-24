package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RunTest2 {
    @Test
    void test() {
        val path = CompileUtil.compile("Test6.java", "TestUtil.java");
        VJVM.init(VMOptions.builder().bootstrapClassPath("lib").userClassPath(path.toString()).entryClass("testsource/Test6").build());
        FileUtil.DeleteRecursive(path.toFile());
    }
}
