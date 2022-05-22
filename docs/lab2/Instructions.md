---
nav_order: 1
parent: Lab2 字节码解释执行
---

# Lab 2.2 实现更多指令

在 Lab 2.1 中，我们成功运行了一个 HelloWorld 程序。现在，你需要按照手册实现更多
指令，以支持运行各种程序。

## 需要实现的指令

[手册第 7 章](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-7.html)包
含了完整的指令列表。

如果一条指令可能对多种数据类型进行操作，我们限定操作数只能是基本类型。引用类型我
们此处暂不考虑。

### Constants

实现除 `aconst_null` 以外的全部指令。

### Loads

实现除 `aload`、`aload_0`、`aload_1`、`aload_2`、`aload_3`、`iaload`、`laload`、
`faload`、`daload`、`aaload`、`baload`、`caload` 和 `saload` 以外的全部指令。

### Stores

不需实现与 loads 中未实现指令相对应的 store 指令。

### Stack

需要实现全部指令。

### Math

需要实现全部指令。

### Conversions

需要实现全部指令。

### Comparisons

需要实现除 `if_acmpeq`、`if_acmpne` 以外的全部指令。

### Control

需要实现 `goto`、`ireturn`、`freturn`、`lreturn`、`dreturn` 与 `return`。

### References、Extended 与 Reserved

暂不需实现除 `invokestatic` 以外的其它指令。

## 如何减少 copy-pasting

觉得这次 Lab 需要实现的指令很多？并且很多指令都是大同小异，懒惰的你忍不住想要依
靠复制粘贴来减少工作量？

事实上，框架中已经提供了一个很好的模板来帮助你减少复制粘贴。我们将 `XCONST_Y` 类
的代码展示如下：

```java
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XCONST_Y<T> extends Instruction {
  private final T value;
  private final BiConsumer<OperandStack, T> pushFunc;
  private String name;

  public static final XCONST_Y<Integer> ICONST_M1(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(-1, OperandStack::pushInt, "iconst_m1");
  }

  public static final XCONST_Y<Integer> ICONST_0(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(0, OperandStack::pushInt, "iconst_0");
  }

  public static final XCONST_Y<Integer> ICONST_1(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(-1, OperandStack::pushInt, "iconst_1");
  }

  public static final XCONST_Y<Integer> ICONST_2(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(2, OperandStack::pushInt, "iconst_2");
  }

  public static final XCONST_Y<Integer> ICONST_3(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(3, OperandStack::pushInt, "iconst_3");
  }

  public static final XCONST_Y<Integer> ICONST_4(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(4, OperandStack::pushInt, "iconst_4");
  }

  public static final XCONST_Y<Integer> ICONST_5(ProgramCounter pc, MethodInfo method) {
    return new XCONST_Y<Integer>(5, OperandStack::pushInt, "iconst_5");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    pushFunc.accept(stack, value);
  }

  @Override
  public String toString() {
    return name;
  }
}
```

虽然只有一个类，但这个文件已经实现了 7 条指令。我们来看看它是如何做到的。

首先我们注意到，对于 `iconst_m1`、`iconst_0` 等指令而言，他们唯一不同的地方便是
压到操作数栈的常量值。指令运行的流程都是相同的：

```
ICONST_X.run(thread):
  stack = 当前线程顶部栈帧的操作数栈
  stack.push(需要压栈的常量)
```

因此，我们将这一共同的过程写在 `run` 方法当中，而将不同指令的常量保存为类的
`value`属性。

在解码时，我们需要填入不同的 `value` 属性。因此，我们分别编写了 `ICONST_M1`、
`ICONST_0`、`ICONST_1` 等方法，用于解码不同的指令。这些指令以不同的参数调用
`XCONST_Y` 的构造函数（构造函数通过 `@AllArgsConstructor` 生成，该注解的作用详见
[文档](https://projectlombok.org/features/constructor)）。

最后，我们在 `Decoder.decodeTable` 中填入这几个方法，让解码器可以正确识别对应的
指令。

`XCONST_Y` 类并不局限于 `int` 类型。该类还有一个 `pushFunc` 属性用于将常量压到栈
上。实现不同类型时，你可以使用 `OperandStack` 的不同方法，比如对于 `fconst_0`：

```java
public static final XCONST_Y<Float> FCONST_0(ProgramCounter pc, MethodInfo method) {
  return new XCONST_Y<Float>(0f, OperandStack::pushFloat, "fconst_0");
}
```

许多指令都可以使用类似的方式实现。正因如此，参考实现只用了 23 个类就实现了 Lab 2
的全部指令（你可以猜到哪些类实现了哪些指令吗？）：

```
$ tree
.
├── comparisons
│   ├── IFCOND.java
│   ├── IF_XCMPCOND.java
│   ├── LCMP.java
│   └── XCMPCOND.java
├── constants
│   ├── LDCX.java
│   ├── NOP.java
│   ├── XCONST_Y.java
│   └── XPUSH.java
├── control
│   ├── GOTO.java
│   └── XRETURN.java
├── conversions
│   └── X2Y.java
├── loads
│   └── XLOAD.java
├── math
│   ├── IINC.java
│   ├── LIOPR.java
│   ├── XNEG.java
│   └── XOPR.java
├── references
│   ├── INVOKESTATIC.java
├── reserved
│   └── BREAKPOINT.java
├── stack
│   ├── DUPX.java
│   ├── DUPX_XY.java
│   ├── POPX.java
│   └── SWAP.java
└── stores
    └── XSTORE.java
```

我们强烈建议你使用类似的方式来减少 copy-pasting，否则你将体验到“一次编写，n 处
bug”的厉害。

---

恭喜！你现在已经得到了一个[图灵完
备](https://en.wikipedia.org/wiki/Turing_completeness)的虚拟机。在接下来的 Lab
3 中，我们将关注类的链接、初始化等过程，支持更多 Java 程序的运行。

> 关于图灵完备性的一些有趣事实
>
> - [C++ 模板是图灵完备
>   的
>   ](https://matt.might.net/articles/c++-template-meta-programming-with-lambda-calculus/)
>   。
>
> - SQL 是图灵完备的，你甚至可以[用 SQL 来做光
>   追](https://www.pouet.net/prod.php?which=83222)。
>
> - x86 MOV 指令也是图灵完备的，有人写了一个[只生成 MOV 指令的编译
>   器](https://github.com/Battelle/movfuscator)。
>
> - 你使用的正则表达式[也是图灵完备
>   的
>   ](http://neilk.net/blog/2000/06/01/abigails-regex-to-test-for-prime-numbers/)
>   。
>
> - 没有哪台计算机是图灵完备的（[因为内存有
>   限](https://en.wikipedia.org/wiki/Turing_machine)）。
>
> 你可以在[这里](https://www.gwern.net/Turing-complete)找到更多图灵完备的例子。
