---
has_children: true
permalink: /
title: Lab0 开始之前
nav_order: 0
---

# 大作业简介

## JVM 是什么

> The Java Virtual Machine is an abstract computing machine. Like a real
> computing machine, it has an instruction set and manipulates various memory
> areas at run time. It is reasonably common to implement a programming language
> using a virtual machine; the best-known virtual machine may be the P-Code
> machine of UCSD Pascal.

上面摘自 [The Java® Virtual Machine Specification Java SE 17
Edition](https://docs.oracle.com/javase/specs/jvms/se17/jvms17.pdf)。简而言之，
JVM 就是一个执行 [**指令**
](https://en.wikipedia.org/wiki/Instruction_set_architecture) 的程序。JVM 规范中
定义了约 200 条指令，每一条负责完成一件特定的事情。通过将这些指令排列成一个**指
令序列**并逐条执行，我们可以完成 C、Java 等编程语言可以做的任何事情。

为了执行这些指令，JVM 中包含了
[堆](https://en.wikipedia.org/wiki/Heap_(programming))、
[栈](https://en.wikipedia.org/wiki/Call_stack)、常量池、局部变量表、
[PC](https://en.wikipedia.org/wiki/Program_counter) 等结构，每条指令做的事情最终
变成对其中的数据进行操作。

### JVM 中的指令

作为一个 [基于栈的虚拟机](https://en.wikipedia.org/wiki/Stack_machine) JVM 中大
部分的指令都是三种形式之一：1. 将数据压入栈顶，2. 将栈顶数据存入其他地方，3. 取
出栈顶数据进行计算并将计算结果压栈。

例如下面这段代码：

```java
int a = 4, b = 5;
int c = a + b;
```

经过编译后会生成以下指令，同学们可以尝试在纸上模拟这段程序的执行过程：

```
0: iconst_4 // 将常数 4 压栈
1: istore_1 // 将栈顶的数存入变量 a
2: iconst_5
3: istore_2
4: iload_1
5: iload_2 // 将 a 和 b 的值压栈
6: iadd // 取出栈顶两个数取出相加，压回栈中
7: istore_3 // 将栈顶存入变量 c
```

对于更复杂的程序，它们经过编译后生成的指令序列会更长，使用的指令数量也更多，但在
本质上和上面这个并没有本质的区别：JVM 做的事情就是不停地执行指令，只要你将全部的
指令完整实现，你就可以运行任何 Java 程序。

## 在这个大作业中，我将学到什么

对于大一的你而言，以上许多概念可能十分陌生，但不用担心，随着大作业的进行，你将逐
步了解它们，并自己实现一个（简化的）JVM。

当然，学习 JVM 本身并不是这个大作业的最终目的。通过这个大作业，我们还希望让你学
到：

TODO

- 程序的机器级表示、程序的执行
- 编写、维护中等规模项目的能力
- 有效提高生产力的工具、库等

## 大作业内容一览

按照我们目前的构想，大作业会分为三个部分：

- Lab1：Class 文件查找与解析

  Class 文件是 Java 语言的编译产物，每个文件中包含了一个 Java 类的成员、方法和常
  量等数据。JVM 运行时从指定的 Main Class 开始，动态地把所需的类加载到内存中，并
  执行其中的方法。

  在执行 Java 代码之前首先需要定位 Class 文件并解析其内容，因此我们在 Lab1 将会
  完成这部分工作。

- Lab2：基本命令的执行

  任何一个指令集都有一些相似的基本指令：算术、内存读写、控制流转换等。在这个 Lab
  中我们将实现这些指令。事实上，有了这些指令之后，JVM 就已经是 [图灵完
  全](https://en.wikipedia.org/wiki/Turing_completeness) 的：任何计算机可以做的
  事情，你面前的这个程序都可以做。

- Lab3：运行时环境与类

  [类](https://en.wikipedia.org/wiki/Class_(computer_programming)) 是 Java 语言
  的核心，JVM 中也对它提供了特殊支持。在这个 Lab 中我们将实现类的初始化、虚函数
  调用等功能。在完成这些之后，你的 JVM 就能正常运行大部分的 Java 程序。

## JVM 大作业的历史

你们并不是第一届被这个大作业折磨的软院人。🤣

初代 JVVM 原本是 [wym 童鞋](https://github.com/wym0120) 的毕业设计，在他做软工一
助教时被用于设计 19 级大作业。今年，[wyh](https://github.com/wyh2023) 和
[czg](https://github.com/amnore) ~~发扬软院祖传大作业的优秀传统~~对 JVVM 的代码
和作业进行了重构。希望能给大家带来一个完成度更高，各部分作业分配更合理的版本。
