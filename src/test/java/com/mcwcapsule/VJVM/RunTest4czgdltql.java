package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;

public class RunTest4czgdltql {
    @Test
    void test(){
        val path = CompileUtil.compile("Test8.java", "TestUtil.java");
        VJVM.init(VMOptions.builder().entryClass("testsource/Test8").bootstrapClassPath("lib").userClassPath(path.toString()).build());
        FileUtil.DeleteRecursive(path.toFile());

    }


}
