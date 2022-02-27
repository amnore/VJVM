---
nav_order: 0
parent: Lab1 类加载与解析
---

# 1.1 ClassLoader

> 本章对应 JVM 规范内容：5.3.1、5.3.2，另请参阅 [ClassLoader 的 JDK 文
> 档](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html)。

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

当然，不是所有的 ClassLoader 都需要遵守 parent-first 策略。
[Tomcat](https://tomcat.apache.org/tomcat-10.0-doc/class-loader-howto.html) 等应
用为了满足自身的特殊需求就采用了 child-first 的方式。

## Read The Friendly Code (2)

作为 Lab 1 的引导，我们先来看看框架代码的逻辑。

[前文]({{ site.baseurl }}{% link lab0/CodeIntro.md %})提到，代码的入口是
`vjvm.vm.Main` 类中的 `main` 方法，此外还有 `Run` 和 `Dump` 两个类用于实现执行
Java 程序和打印 class 文件信息两个功能。我们在 Lab 1 中将实现 `Dump`。

在我们执行 `dump` 命令时，框架会调用解析命令行参数并调用 `Dump.call` 方法。在这
个方法中，我们做了以下几件事：

- 创建一个 VMContext。在框架代码中，一个 context 包含了运行 JVM 所需的全部数据。
  Lab 1 中只有两个 ClassLoader：bootstrapLoader 与 userLoader。这两个 loader 在
  构造函数中被初始化。
- 尝试用 userLoader 加载所需的类。如果加载失败则报告错误。
- 如果加载成功，在 `dump` 方法中输出这个类的信息。

在对 `dump` 命令做的事有了一个宏观上的认识之后，我们继续来看创建 context 的过程。

在 `VMContext.java` 中，我们调用了 `ClassLoader` 的构造函数来进行初始化。这个构
造函数接受三个参数：亲代加载器（用于实现双亲委托）、搜索路径和 `context` 自身
（保存以备后用）。搜索路径由一个 `ClassSearchPath` 的数组构成。其中，
bootstrapLoader 的路径为系统 JDK 路径，而用户加载器的路径由
`ClassSearchPath.constructSearthPath` 解析生成。

接下来是加载 class 的部分。我们将 `ClassLoader.loadClass` 方法留空，你需要按照双
亲委托的逻辑来实现类的加载。在从自己的加载路径中搜索 class 时，你需要依次调用
`searchPaths` 这个数组中每一个的 `findClass` 方法，并用找到的第一个 class 文件来
创建类。

对于类的创建，我们调用了 `JClass` 的构造方法。你可以在 Lab 1.1 中把这个方法留空，
我们会在 Lab 1.2 中实现它。

代码中需要你自己实现的部分我们已用 `UnimplementedError` 标识出来。你可以使用以下
命令来查找所有未实现的内容：

```
$ grep -irH UnimplementedError
```

> 修改框架代码
>
> 我们所有的测试都通过命令行进行（请参阅 test 目录中的样例）。因此，在维持命令行
> 接口不变的前提下，你可以任意修改框架代码。事实上，我们鼓励你按照自己的喜好重构
> 框架，并与我们交流你对代码设计的想法。
>
> 需要注意的是，我们在以后的 Lab 中会向框架加入新的代码。如果你对框架代码的接口
> 做了修改，在加入我们的代码时你可能需要进行相应的调整。不用害怕，这正是你学习
> `git merge`，`diff` 等工具的好机会。

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

在框架中，我们已为你准备好了加载类的接口：`ClassLoader.loadClass`。该方法接受一
个字符串，返回加载的类。在 Lab 1.2 中，你将解析 class 文件并填入返回的类。需要注
意的是，这里传入的不是 class 名称，而是它的描述符（descriptor）。如
`java.lang.String` 类对应 `Ljava/lang/String;`。对于 descriptor 我们将在 Lab 1.2
中再次提及。

你在多次加载同一个类时应返回同一个对象，而非多个拷贝。虽然受限于测试方式，我们在
本次 lab 中无法测试这一要求，但在以后的 lab 中你会遇到下面这种代码：

```java
var a = loader.loadClass("Ljava/lang/String;");
var b = loader.loadClass("Ljava/lang/String;");
assert a == b;
```

在查找 class 文件时，Lab 1 中有以下两种路径：

1. 搜索单个目录

   在指定 `/foo` 为加载路径时，如果加载 `bar.A` 类，你应该查找
   `/foo/bar/A.class` 文件。

2. 单个 Jar 文件

   Jar 文件本质上是一个 zip 压缩包，将多个 class 文件打包在一起。在从
   `/foo/bar.jar` 中加载 `baz.B` 时，你应该读取该文件并搜索其中的 `baz/B` 路径。
   JDK 中提供了 `JarFile` 类来读取 jar 文件。

   > 你可以使用 `bsdtar -tf <jarfile>` 命令查看 Jar 文件的内容。

这两种加载路径事实上具有同样的接口（interface）：给定一个 class，从中搜索对应的
文件。我们将这个接口抽象成了 `ClassSearchPath` 类。

Bootstrap Loader 从 `System.getProperty("sun.boot.class.path")` 这个路径下搜索系
统类，User Loader 从命令行的 `-cp` 参数中读取加载路径并加载相应的类。该参数可能
包含一个或多个加载路径，每两个之间以路径分隔符（path separator）隔开。对于类
Unix 环境，separator 为 `:`，Windows 下则是 `;`。你可以使用
`System.getProperty("path.separator")` 获取路径分隔符。比如，`/a:/b.jar` 中包含
了两个路径，第一个是 `/a` 这个目录，第二个是 `/b.jar` 这个 jar文件。

> 利用接口提高代码可维护性
>
> 我们当然可以直接在 `loadClass` 方法中直接解析三种加载路径并完成加载，但如果以
> 后需要添加新的加载路径应该怎么办呢？如果用这种方式，我们就需要插入更多的解析和
> 判断。4 种情况或许还在可控范围之类，但如果有 10 种、100 种，那么代码就会变得冗
> 长无比并且很有可能充满错误了。
>
> 将“搜索类”这个动作抽象成一个接口之后，我们可以在多个类中分别去实现它。每个类只
> 需要关心自己查找的方式，使用这个接口的代码也不需要关心具体有哪些查找路径。如此，
> 代码的可读性和可维护性得到了巨大的提升。
>
> 与这种接口常常相伴的，是“工厂方法”，即 `ClassSearthPath.constructSearthPath`。
> 这个方法的作用时解析字符串并为其中每一个路径构造一个 `ClassSearchPath` 的子类。
> 这样我们在 `ClassLoader` 中也不需要关心如何解析路径了。

在成功找到所需类时，我们要求你的命令返回 0；找不到时需返回非 0 值。你可以利用标
准错误流 `System.err` 输出任意调试信息，但请勿使用标准输出流 `System.out`。我们
在之后的 Lab 中会将其中的信息作为测试输出。
