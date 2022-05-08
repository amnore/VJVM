---
nav_order: 0
parent: Lab2 字节码解释执行
---

# Lab 2.1 运行时环境（一）

## Java 字节码

在 Lab 1.2 中，你使用 `javap` 命令打印了 `A.class`。（没有？快回去看看！）其中大
部分的内容你已经自己实现了解析，已经比较熟悉了。然而，还有一个重要部分我们仍未涉
及：Java 程序的字节码。我们把其中的每一条称为一条指令，把 JVM 支持的全部指令种类
称为它的指令集。你可以在以下样例中 `Code:` 之后找到它们：

```
public static void main(java.lang.String[]);
  descriptor: ([Ljava/lang/String;)V
  flags: (0x0009) ACC_PUBLIC, ACC_STATIC
  Code:
    stack=2, locals=2, args_size=1
       0: new           #8                  // class test/A
       3: dup
       4: invokespecial #13                 // Method "<init>":()V
       7: astore_1
       8: return
```

### 一组最简单的指令

在介绍 Java 字节码之前，我们先来看看一个最简单的指令集。假设我们的程序中只有三个
`int` 型变量：`x`、`y` 和 `z`，所有的操作都在这两个变量上进行。我们的指令集由以
下几种指令组成：

```
dst = constant # 将某个变量赋值为一个常量值
dst = src1 + src2 # 将某个变量赋值为两个变量的和
PRINT src # 打印一个变量
GOTO i # 跳转到第 i 条指令
```

在执行时，程序会从第一条指令开始依次执行。使用这组指令，我们可以写出一个打印斐波
纳契数列的程序：

```
1: x = 0
2: y = 1
3: z = x + y
4: x = y
5: y = z
6: PRINT x
7: GOTO 3
```

从这个最简单的指令集中，我们可以发现指令具有三种主要功能：

- 运算

  通过一条指令，我们可以对程序中的变量（或常量）进行计算，然后把结果存入另一变量
  中。`dst = constant` 及 `dst = src1 + src2` 都属于这种类型。

- 控制程序执行

  通过 `GOTO` 指令，我们实现了循环的结构。我们把 `GOTO` 称为无条件跳转。此外，有
  的指令还可以比较两个变量的大小，并根据比较结果决定是跳转到指定位置还是继续执行
  下一条指令。我们把这种称为有条件跳转。

- 与外界进行交互

  `PRINT` 指令便是这种类型，此外还可以从外界读取一个变量的值（即相当于 C 的
  `scanf`）。Java 通过另外的方式完成这些功能，我们将在稍后提到。

## JVM 的运行环境

“那么，现在可以言归正传，介绍 JVM 的指令集了吗？”很遗憾，还不能。作为一个现实中
的指令集，JVM 中的指令远比我们 3 个变量，4 种指令的玩具复杂。其中的许多指令并不
直接对变量进行操作，因此我们需要首先熟悉 Java 程序的运行时模型。

- [ ] TODO: 介绍（或抄）JVM 的运行环境

## Read The Friendly Code (3)

在 Lab 2 中，我们增加了 VJVM 运行时环境与指令执行的框架。同样地，在开始编码之前
我们现在看看新增的代码。

### Code 属性

为了执行字节码，我们需要首先将它保存在对应的方法中。Class 文件的字节码在方法的
`Code` 属性里。我们已将 `Code` 类给出，你需要实现它的构造方法，并在`MethodInfo`
中调用它解析 `Code` 属性。由于我们不会在 Lab 2 中实现异常处理，你可以跳过
`exception_table` 的解析。

### JThread 与 JFrame

这两个类分别表示 JVM 的线程与栈帧。我们已将其中的方法全部实现，你无需修改其中的
代码，但最好能理解各个方法的含义。

虽然一个 VJVM 中可以保存多个线程的信息（`VMContext` 中使用 `ArrayList` 保存
`JThread`），但我们不会涉及到多线程相关内容。

<figure>
  <img src="https://imgs.xkcd.com/comics/new_bug.png" />
  <figcaption>
    <link href="https://en.wikipedia.org/wiki/Heisenbug">Concurrency</link>
    is
    <link href="https://www.bilibili.com/video/BV13u411X72Q">HARD!</link>
    (<link href="https://xkcd.com/1700/">New Bug</link>
    by xkcd /
    <link href="https://creativecommons.org/licenses/by-nc/2.5/">CC BY-NC 2.5</link>)
  </figcaption>
</figure>

### OperandStack 与 Slots

在 JVM 中，我们多处使用了相似的方式来存储数据：在局部变量表和操作数栈中，每个位
置都是 4 字节，且 `long` 与 `double` 占用连续两个位置。框架把这种存储方式单独实
现成 `Slots` 类以便共用代码。

我们规定每个 `int` 所占空间为一个槽位（Slot），`long` 与 `double` 占用两个槽位，
且使用第一个的下标访问。在初始化时，我们能够确定最多使用的 slots 数量 `slotSize`，
它可能来自 `Code` 属性中的 `max_stack` 或 `max_locals`。对于每一种基本数据类型，
我们都提供了对应的 getter 与 setter 方法（无 get 与 set 前缀），其中 `byte`、
`char` 与 `short` 类型应转换成 `int` 存储。除此之外，我们还有 `value` 方法用于获
取不确定类型的变量。你不是必须要实现这个方法，它的作用我们将在后面提到。Slots 的
大小可通过 'size' 方法获得。`copyTo` 方法用于将一个 `Slots` 中的内容复制到另一个
中，我们在方法调用时会使用它把实际参数复制进子方法的局部变量表。

我们规定对于 `Slots` 中的每个位置，set 时的类型必须和 get 时的类型相同。字节码已
经保证了这一点，但我们建议你在 `Slots` 中维护每个槽的类型信息并在运行时检查。

> 既然已经保证了类型相同，为什么还要做检查？
>
> 字节码只保证了进行操作的指令是正确的。然而在实现中，你仍然有可能写出导致操作类
> 型不同的 bug。在 `Slots` 中检查可以帮助你更早地发现并定位 bug。如果要等到程序
> 运行结果不对再回过头去找，那你可能就会白白浪费掉一上午甚至更多的时间。
>
> 更一般地，你在编写一个模块的代码时应该对用户输入、传入参数等保持一定程度的不信
> 任，应检查输入是否是你期望的值而不是直接丢给代码处理。这种思想被称为[防御式编
> 程](https://en.wikipedia.org/wiki/Defensive_programming)。

使用 `Slots`，我们可以很方便地实现操作数栈。`OperandStack` 只需额外维护栈顶的位
置，在压栈或出栈时自动更新。

我们将 `Slots` 与 `OperandStack` 的全部方法留空，你需要自己实现它们。对于
`Slots`，你需要自己选择存储变量的方式。
