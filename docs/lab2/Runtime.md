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

> 程序是如何从源码被编译为指令的？
>
> 我们还是以斐波纳契数为例：
>
> ```c
> int main() {
>   int x, y, z;
>   x = 0;
>   y = 1;
>   do {
>     z = x + y;
>     x = y;
>     y = z;
>     print(x);
>   } while(true);
> }
> ```
>
> 可以看出，上面的 C 代码和指令级别的程序在结构上有一定的对应关系。例如，最简单
> 的加法和赋值语句都有着直接对应的指令，而 `do {...} while(true)` 语句则对应着
> `3: ... 7: GOTO 3` 中一块连续的指令。
>
> 事实上，编译的一个主要步骤便是自顶向下地把源代码中的语句翻译为指令。比如，我们
> 首先会看到 `do {...} while(true)` 语句。我们记下此时的地址，然后递归地翻译花括
> 号中的四条语句。最后，我们生成 `GOTO` 指令，并填入之前记录下的地址。你们会在
> 《编译原理》课程中更具体地学习如何把一门高级语言编译成可以直接在机器上运行的机
> 器代码。

## JVM 的运行环境

限于篇幅，我们不会在本文档中介绍 JVM 的运行环境中，而是给出相关链接让你们自行阅
读。框架代码中许多的类都与运行环境中的某个数据结构直接对应，因此你需要首先理解这
部分内容。（在 Lab 2.1 中，我们不会涉及到堆区）

关于 Java 运行环境最权威的描述自然是在手册中。相关内容可在[手册第二
章](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html)找到。

[JVM 底层原理最全知识总
结](https://doocs.github.io/jvm/01-jvm-memory-structure.html)中包含了运行时各个
结构的概览。如果你觉得手册内容难以理解可以参考这个文档的内容。

《深入理解Java虚拟机：JVM高级特性与最佳实践（第3版）》中也包含了对 JVM 运行环境
的详细介绍。我们不会在此处给出链接，你可以自行购买或上网搜索电子书版本。书中第
2.2 节包含了各个数据区域的一览；8.2 节更详细地介绍了与 Lab 2.1 最相关的栈帧结构。

## Read The Friendly Code (3)

在 Lab 2 中，我们增加了 VJVM 运行时环境与指令执行的框架。同样地，在开始编码之前
我们现在看看新增的代码。

### JThread 与 JFrame

“线程”是 JVM 中程序的一个执行流，每个线程都有自己的栈。我们在框架代码中使用
`JThread` 代表一个线程，`JFrame` 代表一个线程的全部栈帧。我们已将其中的方法全部
实现，你无需修改其中的代码，但最好能理解各个方法的含义。

虽然一个 VJVM 中可以保存多个线程的信息（`VMContext` 中使用 `ArrayList` 保存
`JThread`），但我们不会涉及到多线程相关内容。

<figure>
  <img src="https://imgs.xkcd.com/comics/new_bug.png" />
  <figcaption>
    <a href="https://en.wikipedia.org/wiki/Heisenbug">Concurrency</a>
    is
    <a href="https://www.bilibili.com/video/BV13u411X72Q">HARD!</a>
    (<a href="https://xkcd.com/1700/">New Bug</a>
    by xkcd /
    <a href="https://creativecommons.org/licenses/by-nc/2.5/">CC BY-NC 2.5</a>)
  </figcaption>
</figure>

### OperandStack 与 Slots

在 JVM 中，我们多处使用了相似的方式来存储数据：在局部变量表和操作数栈中，每个位
置都是 4 字节，且 `long` 与 `double` 占用连续两个位置。框架把这种存储方式单独实
现成 `Slots` 类以便共用代码。

我们规定每个 `int` 所占空间为一个槽位（slot），`long` 与 `double` 占用两个槽位，
且使用第一个的下标访问。Slots 的下标从 0 开始。在初始化时，我们能够确定最多使用
的 slots 数量 `slotSize`，它可能来自 `Code` 属性中的 `max_stack` 或 `max_locals`。
对于每一种基本数据类型，我们都提供了对应的 getter 与 setter 方法（无 get 与 set
前缀），其中 `byte`、`char` 与 `short` 类型应转换成 `int` 存储。Slots 的大小可通
过 `size` 方法获得。在进行函数调用时，参数首先从操作数栈的栈顶弹出，然后被复制进
被调用方法栈帧的局部变量表中。我们通过 `copyTo` 与`popSlots` 支持这种操作。

我们规定在使用具体类型的 get 与 set 方法时，每个位置 set 的类型必须和 get 的类型
相同。字节码已经保证了这一点，但我们建议你在 `Slots` 中维护每个槽的类型信息并在
运行时检查。

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

### Code 与 ProgramCounter

为了执行字节码，我们需要首先将它保存在对应的方法中。Class 文件的字节码在方法的
`Code` 属性里。我们已将 `Code` 类给出，你需要实现它的构造方法，并在`MethodInfo`
中调用它解析 `Code` 属性。由于我们不会在 Lab 2 中实现异常处理，你可以跳过
`exception_table` 的解析。

我们也封装了程序计数器。在 VJVM 中，每个栈帧都有自己独立的 PC，指向所执行方法的
字节码，而整个线程使用最顶部栈帧的 PC。`ProgramCounter` 使用一个 `ByteBuffer` 来
包装原始字节码，并提供了 `byte_`、`ubyte` 等方法读取数据同时向前移动。这样，在我
们读取完一条指令并解码后，PC 自然就指向了下一条指令。

### Instruction 与 JInterpreter

最后，我们来看看 VJVM 运行一个 Java 程序的流程。运行程序的命令通过 `vm.Run` 类实
现，其中会首先初始化运行时环境，然后调用 `VMContext.run` 执行程序。在`VMContext`
中，我们会首先加载用户指定的主类并寻找其中的 `main` 方法。在找到之后我们便将它交
给解释器 `JInterpreter` 执行（我们在这里额外传入了一个 `Slots`，代表 `main` 函数
的参数。

解释器首先利用方法和参数构造一个栈帧，在 `JFrame` 构造方法中将参数压栈，然后进入
程序主循环 `run` 方法解释执行指令。

在 VJVM 中，我们把执行一条指令分为解码与执行两部分。每个指令通过公共的
`Instruction.run` 接口执行，而解码则由 `Decoder.decode` 完成。

我们使用了一个巨大的表来解码指令。`Decoder.decodeTable` 有 256 个表项，其中
的每一项对应指令第一个字节 `opcode`。解码时，我们首先读取 `opcode`，然后查表调用
相应函数来解码指令。

### 方法调用

我们通过 `JInterpreter.invoke` 进行调用。该方法接受三个参数：需调用的方法、当前
线程与参数。它会创建一个新的栈帧，将参数复制到新栈帧的局部变量表中，并将栈帧压到
当前线程的栈顶。在完成准备之后，解释器会进入 `run` 执行该方法的代码。虚拟机启动
时，我们会在 `VMContext.run` 中调用 `main` 函数。

框架代码使用 native 方法实现了输入输出功能。你可以在 `testdata` 中找到 `IOUtil`
类，它提供了打印数据到标准输出和从标准输入读取数据的方法。相应地，解释器中对于这
几个方法作了特殊处理：当其中一个方法被调用时，我们不会解释执行 Java 代码（几个方
法也根本没有 `Code` 属性），而是直接在 VM 中实现了输入和输出的功能。native 方法
的执行通过 `runNativeMethod` 实现。

事实上，标准的 JVM 中输入输出也是通过类似方式实现的。JVM 本身只能进行计算，并不
能读取或写入数据。为了完成 IO 任务，JVM 将 native 方法绑定到对应的 C 函数上，并
通过 C 函数与操作系统交互。

## 运行你的第一个 Java 程序

现在，让我们来实现框架中缺失的部分。在完成这些内容后，你将可以成功运行
HelloWorld 程序。

### 补全运行时数据结构

框架的 `Slots` 与 `OperandStack` 尚未实现，你需要自己实现其中内容。`Slots` 有多
种实现方式，你可以用 `Object[]` 数组保存，也可以保存原始的字节，并在使用时转换为
对应类型。

在解析类文件时，你需要额外解析方法的 `Code` 属性，并填入相应成员中。此外，你还需
要实现 `JClass.name` 方法，返回当前类的二进制名称（如 `java/lang/String`）。

### 实现 invokestatic 指令

测试代码的输入输出都通过 native 方法实现。为了调用这些方法，你需要补全
`invokestatic` 指令。

[JVM 规
范
](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.invokestatic)
中关于 `invokestatic` 的行为有十分详细的说明。现阶段你只需要实现以下内容：

- 解析：

  除 opcode 外，每条 `invokestatic` 还编码了一个 `unsigned short` 指向当前类常量
  池中一个 `CONSTANT_Methodref_info` 常量，你可以在 [JVM 手
  册
  ](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.invokestatic)
  中找到该指令的编码格式。你需要首先加载该方法所在类，然后从加载的类中找到与
  `Methodref` 相符的 `MethodInfo` 并保存至指令中。

- 运行：

  首先从当前方法的操作数栈顶弹出 `argc` 个 slots，即弹出方法调用的参数。然后通过
  `Interpreter.invoke` 调用方法。我们已将 `run` 的代码实现，但你需要编写
  `MethodDescriptors.argc` 以计算参数占用的 slots 数量。

以上两个步骤的伪代码如下：

```
argc(descriptor /* 方法描述符，如 (I[JLjava/lang/String;)V */):
  argc = 0
  n = 0
  while 该方法有第 n 个参数:
    desc = 第 n 个参数的描述符，在以上例子中分别为 I、[J 与 Ljava/lang/String;
    size = desc 对应类型所占的槽位数。Long 和 Double 为 2，其它为 1
    argc += size
    n += 1
  return argc

INVOKESTATIC(pc /* 当前 PC */, method /* 当前指令所在方法 */):
  cp = 当前类的常量池
  methodRef = cp[从 PC 读取下标]
  thisClass = method 所在的类
  jClass = 使用 thisClass 中的 classLoader 加载 methodRef 指向的类
  this.method = 使用 findMethod 在 jClass 中查找与 methodRef 匹配的方法

run(thread /* 执行时的线程 */):
  stack = 线程当前栈帧的操作数栈
  argc = 使用 MethodDescriptors.argc 计算方法参数占用的 slots 数量
  interpreter = 解释器
  args = 从 stack 顶部弹出 argc 个 slots
  使用 interpreter.invoke 调用方法
```

在实现以上内容后，让我们来运行 HelloWorld：

```
$ ./gradlew jar # Windown 用户请使用 gradlew.bat
$ java -jar build/libs/VJVM-0.0.1.jar -cp testdata/build run lab2.HelloWorld
H
e
l
l
o
,

W
o
r
1
d
```

> 运行输出与文档不一样？
>
> 哈哈，我们在实现的指令中埋了一个 bug，你需要找到并修正它。😅接下来我们会介绍框
> 架中提供的帮助你调试指令 bug 的工具——字节码调试器 JMonitor。

## 屠龙宝刀，点击就送

如果你使用过 gdb，那么以下内容或许会让你觉得十分亲切。我们实现的调试器借鉴了
gdb 的许多命令。（如果你没用过，gdb 是一个 native 程序的调试器。如果你上学期写
CPL 作业也是用的 IDE，那么它的调试器后端很有可能就是使用的 gdb）

在调试器中，你看到的一般是一行一行的源代码。你可以在一行上设置断点，打印局部变量
的值。我们把这种调试器称为源代码级调试器（Source-Level Debugger）。然而，在实现
指令时，我们经常需要跟踪每一条指令的执行结果以找出 bug，因此在调试时，我们也需要
能够看到最基本的指令、原始的操作数栈和局部变量表等内容。这种调试器被称为汇编级调
试器（Assembly-Level Debugger）。我们在 Lab 2 中提供的便是一个汇编级调试器。

> 利用汇编级调试器实现源代码级调试器
>
> 在和运行中的程序打交道时，调试器看到的并不是源代码，而是指令。那么，源代码级调
> 试器是如何实现的呢？
>
> 事实上，源程序中的每一条语句都对应着一块连续的（也可能是多块）指令。只要能够知
> 道指令与语句的对应关系，我们就可以在源代码级别显示程序执行到了哪里。Class 文件
> 中每个 Code 属性都有一个可选的 LineNumberTable 属性，其作用便是建立指令与源代
> 码行的映射。
>
> 对于局部变量，Code 也有一个 LocalVariableTable 属性来建立其名称到局部变量表位
> 置的映射。因此我们也能读取到局部变量的信息。
>
> 当然，我们并不要求你解析这两种属性。如果你想要得到一个源代码级调试器可以自行对
> 它们进行解析，并 hack JMonitor 源码加入对应功能。

在执行程序的时候，我们可以加入 `-d` 选项来启动调试器：

```
$ ./gradlew jar # Windows 用户请使用 gradlew.bat jar
$ java -jar build/libs/VJVM-0.0.1.jar -cp testdata/build run -d lab2.HelloWorld
```

### 调试器使用说明

调试器会停在第一条指令，打印当前所在的方法和指令并提示你进行输入。你可以输入 `h`
来获取帮助：

```
> h
Usage:  [COMMAND]
VJVM debugger interface
Commands:
  bt  backtrace
  b   set breakpoint
  c   continue
  d   delete breakpoint
  disas  disassembly current function
  h   print help message
  q   quit
  si  step instruction
  i   info
```

- `bt`：打印栈帧

  该命令会自上而下打印当前函数到最底层方法的调用栈。其中每一行包含栈帧位置、PC在
  方法中的位置和所在的类名、方法名及描述符。可能的输出如下：

  ```
  > bt
  #0    0    in java/lang/String$CaseInsensitiveComparator:<init>:()V
  #1    18   in java/lang/String:<clinit>:()V
  ```

- `b`：设置断点

  该命令会在指定函数设置断点。设置时需以 `${类名} ${方法名} ${偏移量}` 的格式指
  定断点的位置，其中偏移量可省略，默认设置在方法的第一条指令。比如：

  ```
  > b lab2.HelloWorld main # 在 HelloWorld 类的 main 的函数中设置断点
  > b lab2.HelloWorld main 50 # 在 main 函数中偏移量为 50 的指令处设置断点。
  # 偏移量是指令第一个字节距该方法字节码第一个指令的字节数，
  # 可从 javap -verbose 命令的输出中找到
  ```

- `c`：恢复执行

  该命令会恢复程序的执行，直到遇到断点。

- `d`：删除断点

  使用 `d ${i}` 会删除标号为 `i` 的断点。断点标号可使用 `i b` 命令查看。

- `q`：停止程序执行并退出

- `si`：单步执行

  - `si`：单步执行一条指令
  - `si ${n}`：单步执行 `n` 条指令

- `disas`：打印当前函数的指令序列，当前所在位置会以 `*` 标识。当遇到无法译码的指
  令时，该命令会打印之后全部原始字节。如：

  ```
  > disas
  0*   iconst_1
  1    putstatic Z
  4    iconst_0
  5    anewarray java/io/ObjectStreamField
  8    putstatic [Ljava/io/ObjectStreamField;
  11   new java/lang/String$CaseInsensitiveComparator
  14   dup
  15   invokespecial java/lang/String$CaseInsensitiveComparator:<init>:()V
  18   putstatic Ljava/util/Comparator;
  21   return
  ```

  对于未实现的指令，你可以使用 `javap -verbose` 查看原指令序列。

- `i`：打印程序各部分信息，如：

  - `i b`：打印设置的断点
  - `i lo`：打印当前函数的局部变量表
  - `i op`：打印当前函数的操作数栈

当输入为空时，调试器会重复执行上一条命令。你也可以利用这个特性来单步执行多条指令。

### （可选）实现打印操作数栈、局部变量表与添加断点

操作数栈与局部变量表都使用 `Slots` 保存数据，因此你只需实现其中的 `value` 方法便
可打印两者的内容。 `value` 方法接收一个下标，当这个下标没有保存数值时，我们返回
`Optional.empty()`。当该处有值时，我们返回此处保存的值。

> 使用 Optional 而非 null 来表明可能为空的值
>
> Tony Hoare 称自己发明的空引用为 [a billion-dollar
> mistake](https://en.wikipedia.org/wiki/Tony_Hoare#Apologies_and_retractions)。
> 的确，无论是 C 中解引用 NULL 导致程序崩溃，还是 Java 的 NullPointerException，
> 处理空指针都是编写程序时一个令人十分头疼的问题。
>
> 其中很大一部分原因是指针为空是隐式包含在代码中的。编写一个返回指针的函数时，你
> 不能在代码层面保证返回值不为空；而在使用指针前，你也不会被强制要求检查指针是否
> 为空。空指针错误只会在试图解引用时暴露，而此时你的指针可能已经经过了十几层函数
> 传递，根本找不出原本来自哪里了。空指针导致的问题在 Java 这种“一切皆为指针（引
> 用）”的语言中更为泛滥。
>
> 通过使用 Optional，我们显式地表明了返回值可能为空，使用者应该进行检查。在传给
> 其它函数时这种方式也是安全的：要么在传递前进行检查，要么将 Optional 传给别人。
>
> 事实上，很多现代语言（如 Kotlin、Rust 等）在类型系统层面将空值与不可能为空的值
> 区分开来。比如，一个 `String` 必定不为空而 `String?` 可能为空。你不能直接通过
> `String?` 调用 `String` 的方法，必须首先进行空指针检查。这样，一个程序的大部分
> 代码就不用担心空指针的问题了。
>
> 当然，框架代码中大部分地方由于历史原因仍是使用的 `null` 作为空值。你在自己加入
> 新的代码是应避免这种做法。

实现断点则需要更多的工作。`JInterpreter` 中分别有 `setBreakpoint` 和
`removeBreakpoint` 两个方法，分别用于添加一个断点和删除一个断点。`setBreakpoint`
接收需要打断点的方法和断点在字节码中的偏移量（与 `b` 命令含义相同）；
`removeBreakpoint` 接收需要移除的断点在 `breakpoint` 中的下标。

除此之外，你还需要在执行指令的流程中处理断点。具体地，你需要在执行断点指令后将该
处断点暂时禁用（即将该处指令恢复为原指令）并打开调试器；执行完原指令之后再次启用
断点。这部分的伪代码如下：

```
当前断点 = null

while True:
  对指令进行解码
  执行指令
  if 当前断点 != null：
    使用 Breakpoint.enable 启用断点
    当前断点 = null
  if 解释器处于 break 状态，即该指令为断点指令：
    当前断点 = 从全部断点中找到当前指令对应的断点
    使用 Breakpoint.disable 禁用断点
    使用 JMonitor.enter 进入调试器
```

### 调试 HelloWorld

利用调试器，我们可以很容易地单步执行定位到之前碰到的 bug。我们将调试和更正的步骤
作为作业留给你自行完成。（提示：一直单步执行到程序结束，看看是哪些指令得到了不正
确的结果）

---

Lab 2.1 到此结束，请将你的代码提交到 OJ 上。提交方式与之前的 Lab 相同。
