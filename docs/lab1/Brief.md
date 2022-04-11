---
has_children: true
permalink: /lab1/
nav_order: 2
---

# Lab1 类加载与解析

VJVM 的最终目标是实现一个执行 Java 程序的虚拟机，但在这之前我们需要首先进行class
文件的加载和解析。在 Lab1 中，你们将完成这部分内容，并实现一个类似于
[javap](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javap.html)的
程序。

> 你在每个 Lab 中编写的代码将在之后整个大作业中使用。也就是说，如果你贪图一时方
> 便写出了难以维护的代码，IT WILL BITE YOU EVENTUALLY!!! 请仔细思考代码的结构和
> 各个模块的接口设计。

> 在开始之前，请拉取 lab1 的框架代码：
>
> ```
> $ git pull origin
> $ git checkout lab1
> ```
