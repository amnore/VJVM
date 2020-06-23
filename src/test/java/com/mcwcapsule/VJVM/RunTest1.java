package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class RunTest1 {
    @Test
    void test() {
        Path path = CompileUtil.compile("Test5.java", "TestUtil.java");
        VJVM.init(VMOptions.builder().entryClass("testsource/Test5").userClassPath(path.toString()).bootstrapClassPath("lib").build());
        FileUtil.DeleteRecursive(path.toFile());
    }
}
