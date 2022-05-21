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
