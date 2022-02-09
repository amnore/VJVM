# 项目结构

## JVM 一览

一个最简单的 JVM 可以粗略分成两个部分：指令执行引擎和运行时的各种数据结构。（两
部分在本项目中代码量相当，各约 1500 行）

### 运行时数据

1. 堆：保存对象

2. 线程数据

  - 栈：栈中每层函数调用形成一个帧

    - 局部变量表

    - 操作数栈：JVM 是一个基于栈的虚拟机，所有的运算在一个栈中进行。如 iadd 指令：
      从栈顶取出两个 int，相加结果压回栈顶

    - 槽（Slots）：局部变量表和操作数栈以 4B 为一个单位，long 和 double 占两个单
      位。

    - 当前类的常量池

  - PC：通常实现为每个帧保存一个 PC，线程的 PC 为栈顶帧的。

3. 类数据

  - 常量池：保存各种常量（数值类型、字符串、对类、成员、方法的引用）

  - 父类和接口引用

  - 成员信息：类型、可见性、属性（常量值）

  - 方法信息：代码、异常跳转表等均保存在属性中

  - 属性

### 指令执行

指令执行的核心是一个取指-译码-执行的循环。解释器每次从指令流中读取一条指令，根据
opcode 和额外信息完成译码工作，并调用实现这条指令的函数执行。

JVM 规范把指令分为 11 类：

1. constant：立即数/常量池 -> 栈
2. load：局部变量 -> 栈
3. store：栈 -> 局部变量
4. stack：栈顶操作：交换、复制、出栈
5. math：栈上运算：加减乘除、位运算
6. conversion：长度、整形 <-> 浮点
7. comparison：结果 -> 栈，比较并跳转
8. references：field、new、instanceof、方法调用
9. control：goto、return、switch
10. extended：...
11. reserved：breakpoint

## 文件结构

```
vjvm
├── classfiledefs // class 文件中的常量及相关 utils
│   └── attrs, access flags, descriptors, opcodes
├── classloader
│   ├── JClassLoader.java // 类加载器（完全在 jvm 中实现）
│   └── searchpath // 加载器的搜索路径：目录、jar 文件、modules（jdk 9+）
├── interpreter // 指令执行
│   ├── JInterpreter.java // 取指-译码-执行循环
│   └── instruction // 各种指令
├── runtime
│   ├── classdata // class 数据的运行时表示
│   │   ├── attribute // class、field、method 的 attrs
│   │   ├── constant // 常量池内容
│   │   ├── ConstantPool.java
│   │   ├── FieldInfo.java
│   │   └── MethodInfo.java
│   ├── JClass.java
│   ├── JFrame.java
│   ├── JHeap.java
│   ├── JThread.java
│   ├── object // 堆中的对象
│   ├── OperandStack.java
│   ├── ProgramCounter.java
│   └── Slots.java
├── utils
└── vm
    ├── Main.java // 命令行接口：run、dump
    └── VMContext.java // 每个代表一个虚拟机
```

## 一次执行的大致流程

### 初始化

```java
public class VMContext {
    VMContext(String userClassPath) {
        // 解释器
        interpreter = new JInterpreter();

        // 堆
        heap = new JHeap(heapSize, this);

        // 系统类加载器
        bootstrapLoader = new JClassLoader(
            null,
            new ClassSearchPath[]{new ModuleSearchPath(ModuleFinder.ofSystem())},
            this
        );

        // 用户类加载器
        userLoader = new JClassLoader(
            bootstrapLoader,
            ClassSearchPath.constructSearchPath(userClassPath),
            this
        );
    }

    void run(String entryClass) {
        // 线程
        var initThread = new JThread(this);
        threads.add(initThread);

        // 基本类型和 String
        for (var desc: initClasses) {
            var c = bootstrapLoader.loadClass(desc);
            assert c != null;
            c.initialize(initThread);
            assert c.initState() == JClass.InitState.INITIALIZED;
        }

        // 主类
        var entry = userLoader.loadClass(Descriptors.of(entryClass));
        entry.initialize(initThread);
        assert entry.initState() == JClass.InitState.INITIALIZED;

        // 查找并执行 main 函数
        var mainMethod = entry.findMethod("main", "([Ljava/lang/String;)V", true);
        assert mainMethod != null;
        interpreter.invoke(mainMethod, initThread, new Slots(1));
    }
}
```

### 解释器主循环

```java
public class JInterpreter {
    public void invoke(MethodInfo method, JThread thread, Slots args) {
        assert args.size() == method.argc() + (method.static_() ? 0 : 1);

        var frame = new JFrame(method, args);
        thread.push(frame);

        if (method.native_()) {
            runNativeMethod(thread);
        } else {
            run(thread);
        }
    }

    private void run(JThread thread) {
        var frame = thread.top();

        // 每层 java 函数调用，run 递归一层。
        while (thread.top() == frame) {
            var opcode = Byte.toUnsignedInt(thread.pc().byte_());
            if (dispatchTable[opcode] == null)
                throw new Error(String.format("Unimplemented: %d", opcode));

            // 合并解码与执行
            dispatchTable[opcode].fetchAndRun(thread);

            if (thread.exception() != null && !handleException(thread)) {
                thread.pop();
                return;
            }
        }
    }
}
```

### 类加载与初始化

```java
public class JClass {
    // 从 class 文件加载
    public JClass(DataInput dataInput, JClassLoader classLoader);

    // 生成数组类和基本类型
    public JClass(
        JClassLoader classLoader,
        short accessFlags,
        String name,
        String superClassName,
        String[] interfaceNames,
        FieldInfo[] fields,
        MethodInfo[] methods);

    public void prepare() {
        // 计算对象大小
        // 初始化静态成员
        // 填写虚函数表
    }

    public void initialize(JThread thread) {
        // 初始化父类和接口
        // 调用静态初始化函数
    }
}
```
