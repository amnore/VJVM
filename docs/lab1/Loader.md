---
nav_order: 0
parent: Lab1 类加载与解析
---

# ClassLoader

> 本章对应 JVM 规范内容：5.3.1、5.3.2，另请参阅 [ClassLoader 的 JDK 文
> 档
> ](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html)
> 。

## 一个 class 的一生 —— 加载

Java 代码中的每个类都会被编译为一个单独的 class 文件。比如以下代码：

```java
// a.java
class A {
  class B {}
}
```

```
$ javac a.java
$ ls
A.class A$B.class
```

在每个 class 行使它的功能前，JVM 首先需要利用 class 文件中的数据创建运行时的数据
结构。这个步骤被称为“加载”，是由 JVM 和 ClassLoader 共同完成的。（注意以上步骤只
适用于正常的类。数组和基本类型是由 JVM 直接创建的，不需要 ClassLoader 的参与）

更具体地，在需要创建一个 class 时，JVM 会做以下几件事情：

- 首先，每个类的的创建都是在执行另一个类的代码时触发的（除一些特殊情况外）。而触
  发的那个类也有自己的 ClassLoader。每个 ClassLoader 都记录了它已加载的类。JVM
  会首先在其中查找所需的类是否已被加载，如果找到则直接返回这个类。

- 如果没找到，JVM 会请求这个 ClassLoader 提供 class 数据。最常见的方式是在一个路
  径下查找 class 文件并读取其数据。为了查找 class 文件，ClassLoader 可以做以下两
  件事之一：

  - 在自己维护的搜索路径中查找该文件
  - 请求另一个 ClassLoader 查找这个文件

- 如果 ClassLoader 找到了 class，JVM 会利用它提供的数据创建对应的类。

ClassLoader 又分为 Bootstrap Loader 和 User-defined Loader，前者在 JVM 中实现，
而后者自身也是在 JVM 中执行的类。由于 User-defined Loader 的实现需要 native 方法
等复杂机制，我们在此做了简化，将 User-defined Loader 也放到 JVM 中。关于两种
loader 在行为上的区别请自行阅读文档。

## 双亲委托加载机制（Parent-First）

虽然 JVM 规范对 ClassLoader 的加载方式没做规定，但 Java 默认使用了名为
parent-first 的策略：每个 loader（除 Bootstrap Loader 外）均有一个**亲代加载器
（parent）**，在搜索 class 时首先委托亲代进行搜索，找不到时才搜索自己的加载路径。
于是，各个 ClassLoader 之间就形成了如下的委托关系：

<figure>
  <img src="{{ site.baseurl }}{% link assets/loader-hierarchy.png %}" />
  <figcaption>JDK 中各个 ClassLoader 的委托关系，在新版本 JDK 中 Extension
  ClassLoader 和 Application ClassLoader 分别被称为 Platform ClassLoader 和
  System ClassLoader。</figcaption>
</figure>

关于 JDK 中内置 ClassLoader 的更多信息，请阅读 [相应 JDK 文
档
](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ClassLoader.html#builtinLoaders)
。

当然，不是所有的 ClassLoader 都需要遵守 parent-first 策略。
[Tomcat](https://tomcat.apache.org/tomcat-10.0-doc/class-loader-howto.html) 等应
用为了满足自身的特殊需求就采用了 child-first 的方式。

## 实验要求

> 及时 commit 你的更改
>
> 我们会根据你的 Git 日志来评判你是否有抄袭的嫌疑。如果你把全部更改都放在一次
> commit 中，那么我们就有很大可能怀疑你是 copy-paste 的。
>
> 即使我们不检查日志，你也应该养成及时 commit 的习惯。每次 commit 都会生成一份你
> 的仓库当前状态的副本，及时你不小心删除了某个文件也可以从这个副本中恢复。
>
> 我们还建议你将仓库 push 到 [GitHub](https://github.com) 或 [NJU
> Git](https://git.nju.edu.cn) 上，这样即使你删除了电脑上的全部文件也可以在远程
> 仓库中找到备份。
>
> <figure>
>   <img src="{{ site.baseurl }}{% link assets/git-commit-in-case-of-fire.webp %}" />
>   <figcaption>着火时需做的三件事</figcaption>
> </figure>
>
> 你可以使用以下命令来完成 commit：
>
> ```
> $ git add ${需要 commit 的文件}
> $ git status
> $ git commit -m "${描述本次 commit 的内容}"
> ```

在 Lab 1.1 中，我们将实现两个 ClassLoader：第一个为 Bootstrap Loader，负责从系统
JDK 中加载类；第二个为 User Loader，负责从命令行指定的 classpath 中搜索 class。
User Loader 以 parent-first 的方式委托给 Bootstrap Loader。

在框架的 ClassLoader.java 中，我们已为你准备好了加载类的接口：`public JClass
loadClass(String descriptor)`。该方法接受一个字符串，返回加载的类。在 Lab 1.2 中，
你将解析 class 文件并填入返回的类中。需要注意的是，这里传入的不是 class 名称，而
是它的描述符（descriptor）。如 `java.lang.String` 类对应 `Ljava/lang/String;`。
对于 descriptor 我们将在 Lab 1.2 中再次提及。

> 修改框架代码
>
> 我们所有的测试都通过命令行进行（请参阅 test 目录中的样例）。因此，在维持命令行
> 接口不变的前提下，你可以任意修改框架代码。事实上，我们鼓励你按照自己的喜好重构
> 框架，并与我们交流你对代码设计的想法。
>
> 需要注意的是，我们在以后的 Lab 中会向框架加入新的代码。如果你对框架代码的接口
> 做了修改，在加入我们的代码时你可能需要进行相应的调整。不用害怕，这正是你学习
> `git merge`，`diff` 等工具的好机会。

你在多次加载同一个类时应返回同一个对象，而非多个拷贝。虽然受限于测试方式，我们在
本次 lab 中无法测试这一要求，但在以后的 lab 中你会遇到下面这种代码：

```java
var a = loader.loadClass("Ljava/lang/String;");
var b = loader.loadClass("Ljava/lang/String;");
assert a == b;
```

对于加载路径，我们要求你实现以下三种类型：

1. 搜索单个目录

   在指定 `/foo` 为加载路径时，如果加载 `bar.A` 类，你应该查找
   `/foo/bar/A.class` 文件。

2. 单个 Jar 文件

   Jar 文件本质上是一个 zip 压缩包，将多个 class 文件打包在一起。在从
   `/foo/bar.jar` 中加载 `baz.B` 时，你应该读取该文件并搜索其中的 `baz/B` 路径。

   > 你可以使用 `bsdtar -tf <jarfile>` 命令查看 Jar 文件的内容。至于在 Java 中读
   > 取 Jar 文件的方法，请搜索 JDK 文档。

3. System Modules

   Java Module 是 Java 9 中新引入的一种打包机制，每个module 文件本质上是一个 zip
   文件加上 4 bytes 文件头。我们只会在 Bootstrap Loader 中加载 system modules，
   你可以使用 `ModuleFinder.ofSystem()` 获取其加载路径。

这三种加载路径事实上具有同样的接口（interface）：给定一个 class，从中搜索对应的
文件。我们将这个接口抽象成了 `ClassSearchPath` 类。

> 利用接口提高代码可维护性
>
> 我们当然可以直接在 `loadClass` 方法中直接解析三种加载路径并完成加载，但如果以
> 后需要添加新的加载路径应该怎么办呢？如果用这种方式，我们就需要插入更多的解析和
> 判断。4 种情况或许还在可控范围之类，但如果有 10 种、100 种，那么代码就会变得冗
> 长无比并且很有可能充满错误了。
>
> 将“搜索类”这个动作抽象成一个接口之后，我们可以在多个类种分别去实现它。这样，每
> 个类只需要关心自己查找的方式，使用这个接口的代码也不需要关心具体有哪些查找路径。
> 如此，代码的可读性和可维护性得到了巨大的提升。
>
> 与这种接口常常相伴的，是“工厂方法”，你可以在 `ClassSearchPath` 这个基类下找到
> 它。我们在将加载路径转换成具体的类时不可避免地需要根据每种路径创建不同的类，但
> 使用这个接口的代码也不应该关心具体哪种路径创建哪种类。因此，我们将解析路径的工
> 作移到了这样一个单独的方法中：它完成路径的解析，并返回实现了这个接口的一组类。
> 这样，我们把解析路径的负担也从使用者移到了接口的实现者身上。

Bootstrap Loader 仅加载 system modules，User Loader 从命令行的 `-cp` 参数中读取
加载路径并加载相应的类。该参数可能包含一个或多个加载路径，每两个之间以路径分隔符
（path separator）隔开。对于类 Unix 环境，separator 为 `:`，Windows 下则是 `;`。
你可以使用`System.getProperty("path.separator")` 获取路径分隔符。

在成功找到所需类时，我们要求你的命令返回 0；找不到时需返回非 0 值。你可以利用标
准错误流 `System.err` 输出任意调试信息，但请勿使用标准输出流 `System.out`。我们
在之后的 Lab 中会将其中的信息作为测试输出。
