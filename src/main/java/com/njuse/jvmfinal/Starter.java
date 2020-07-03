package com.njuse.jvmfinal;


import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;

public class Starter {
    public static void main(String[] args) {

    }

    /**
     * ⚠️警告：不要改动这个方法签名，这是和测试用例的唯一接口
     */
    public static void runTest(String mainClassName, String cp) {
        VJVM.init(VMOptions.builder().userClassPath(cp).entryClass(mainClassName).build());
    }

}
