---
nav_order: 0
parent: Lab1 类加载与解析
---

# 1.1 ClassLoader

## 实验要求

无论是 `.exe` 可执行程序，还是 Java 代码，它们在运行前都是被保存在硬盘上的。程序
不能直接在硬盘上运行，因此我们需要将它们首先加载到内存当中。

`.exe` 文件的加载由操作系统完成，你们会在后续的《操作系统》课程中学到（？）。而
对于 Java 程序，JVM 中内置了一个被称为 ClassLoader 的模块来加载它们。我们将在本
次作业中实现 ClassLoader。

你或许曾在双击 `.exe` 文件时碰到过“找不到动态链接库”的错误。事实上，大部分程序的
代码并不是保存在一个单独的文件中。除了 `.exe` 外，还有各种库的代码保存在 `.dll
(Windows)`、`.so (Unix)` 和 `.dylib (MacOS)` 文件中。动态链接库（[Dynamic-link
Library](https://en.wikipedia.org/wiki/Dynamic-link_library)）便是 `.dll` 后缀名
的来源。在加载程序时，操作系统会在一些路径下面搜索这个程序需要的动态链接库，如果
找不到便会报出以上错误。

<figure> <img src="{{ site.baseurl }}{% link assets/os-loader.jpg %}" />
  <figcaption>
    操作系统加载可执行文件的细节相当复杂，我们就不在此处详细介绍了。如
    果感兴趣可以自己搜索相关资料。（
    <a href="http://jyywiki.cn/OS/2022/slides/17.slides#/4">doge double
    click</a> by <a href="http://jyywiki.cn/">jyy</a> /
    <a href="https://creativecommons.org/licenses/by-nc/4.0/">CC-BY-NC</a>）
  </figcaption>
</figure>

加载 Java 程序的过程也是相似的。所有的 Java 程序经过编译会生成 `.class` 文件，而
ClassLoader 的作用便是搜索指定路径下的这些文件。如果找到了匹配的 class 文件，
ClassLoader 会把其中数据交给 JVM 其他模块完成加载。

在加载时，JVM 会使用类的全名（包括了类名及它所在的包名）进行搜索。为了更灵活地完
成类的加载，JVM 允许一个 ClassLoader 调用另一个的方法搜索 class 文件。我们把这种
方式称为委托。

VJVM 中有两个 ClassLoader：第一个为 Bootstrap Loader，负责加载 Java 标准库的
class 文件；第二个为 User Loader，负责从用户指定的路径中搜索 class。在本次作业中，
你需要补全相关代码使它们能够正常工作。

User Loader 在查找 class 文件前会首先委托 Bootstrap Loader 查找同一个类，如果找
到则直接返回，否则才会在自己的路径中搜索。因此，我们把 Bootstrap Loader 称为
parent，把 User Loader 称为 child。

你在多次加载同一个类时应返回同一个对象，而非多个拷贝。这意味着你会遇到下面这种测
试用例：

```java
var a = loader.loadClass("Ljava/lang/String;");
var b = loader.loadClass("Ljava/lang/String;");
assertTrue(a == b);
```

看起来很复杂，但 `JClassLoader` 的逻辑实际上可以用下面几行伪代码说明：

```
if 需要加载的 class 已被保存在 definedClass 中：
    返回已加载的 class
else if parent 不为 null：
    使用 parent 加载 class

if parent 未找到相应的 class：
    for searchPaths 中的每一项：
        尝试使用它来加载类，并调用 JClass 构造函数构造类

if 所有的 searchPath 都没有找到需要加载的类：
    返回 null
```

在查找 class 文件时，一个 loader 可能会搜索以下两种路径：

1. 搜索单个目录

   在指定 `/foo` 为加载路径时，如果加载 `bar.A` 类，你应该查找
   `/foo/bar/A.class` 文件。

2. 单个 Jar 文件

   Jar 文件事实上是一个 zip 压缩包，将多个 class 文件打包在一起。在从
   `/foo/bar.jar` 中加载 `baz.B` 时，你应该读取该文件并搜索其中的
   `baz/B.class`路径。JDK 中提供了 `JarFile` 类来读取 jar 文件。（你可以使用
   `jar -tf <jarfile>` 查看 Jar 文件的内容）

我们将通过 `Dump` 命令调用你的代码进行测试。在成功找到所需类时，我们要求你的命令
返回 0；找不到时需返回非 0 值。你可以利用标准错误流 `System.err` 输出任意调试信
息，但请勿使用标准输出流 `System.out`。我们在之后的 Lab 中会将其中的信息作为测试
输出。

以上便是 Lab1.1 的全部要求。接下来，我们会介绍更多 ClassLoader 的相关知识（本次
作业中不会直接用到）和框架代码的逻辑。

> 不知道要做什么？
>
> 这很正常。如果你以前写的全是 AC 即丢的 OJ 题，那么你在第一次拿到这种有一定规模
> 的项目的时候必定很茫然。Lab 1.1 需要你编写的代码事实上只有几十行。你需要做的最
> 重要的事其实是理解框架相关部分的逻辑。如果你不知道该如何上手，请回答以下几个问
> 题：
>
> 1. main 函数在哪？Main.java 中的几个命令在给 main 函数传入什么参数的时候会执行？
>
> 2. 运行时抛出的第一个异常在哪里？为了实现这部分我应该做什么？
>
> 3. 代码正常运行一次需要经过哪些类的哪些方法？
>
> 4. user loader 与 bootstrap loader 之间是如何调用的？
>
> 5. 测试的代码是如何调用 Main.java 中的命令的？
>
> 6. 我至少应该修改哪些地方才能让 `findInDir` 这个测试用例不抛异常？还需要修改什
>    么才能让它通过？（虽然面向用例编程并不是一个好习惯，但它确实可以帮助你理解
>    需要做什么）

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

可以看出，ClassLoader 做的事情本质上就是由一个类的类名找到它的二进制表示，JVM 并
不会关心这个类是从哪来的。你可以从硬盘加载，可以从网络加载，甚至可以在运行时动态
生成类。

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
  Lab 1 使用的两个 loader 在构造函数中被初始化。
- 尝试用 userLoader 加载所需的类。如果加载失败则报告错误。
- 如果加载成功，在 `dump` 方法中输出这个类的信息。

在对 `dump` 命令做的事有了一个宏观上的认识之后，我们继续来看创建 context 的过程。

在 `VMContext.java` 中，我们调用了 `ClassLoader` 的构造函数来进行初始化。这个构
造函数接受三个参数：亲代加载器（用于实现双亲委托）、搜索路径和 `context` 自身
（保存以备后用）。搜索路径是一个 `ClassSearchPath` 的数组。其中，bootstrapLoader
的路径为系统 JDK 路径，通过 `getSystemSearchPaths` 得到；用
户加载器的路径来自 `Dump` 命令的参数（classpath），由
`ClassSearchPath.constructSearthPath` 解析生成。

Classpath 可能包含一个或多个加载路径，每两个之间以路径分隔符（path separator）隔
开。对于类 Unix 环境，分隔符为 `:`，Windows 下则是 `;`。你可以使用
`System.getProperty("path.separator")` 获取路径分隔符。比如 `/a:/b.jar` 中包含
了两个路径，第一个是 `/a` 这个目录，第二个是 `/b.jar` 这个 jar 文件。有多个路径
时，你应该按顺序查找每一个路径，并返回找到的第一个 class 文件。

接下来是加载 class 的部分。在框架中，我们已为你准备好了加载类的接口：
`ClassLoader.loadClass`。该方法接受一个字符串，返回加载的类。在 Lab 1.2 中，你将
解析 class 文件并填入返回的类。需要注意的是，这里传入的不是 class 名称，而是它的
描述符（descriptor）。如 `java.lang.String` 类对应 `Ljava/lang/String;`。对于
descriptor 我们将在 Lab 1.2 中再次提及。我们将 `ClassLoader.loadClass` 方法留空，
你需要按照双亲委托的逻辑来实现类的加载。

本次作业要求的两种加载路径事实上具有同样的接口（interface）：给定一个 class，从
中搜索对应的文件。我们将这一行为抽象成了 `ClassSearchPath` 及它的 `findClass` 方
法。

Lab1.1 的测试代码在 `src/test` 目录下，你可以运行其中的测试代码来弄清楚测试的输
入与输出。在执行测试前，我们会把 `VJVM_TESTRES_PATH` 环境变量指向 `testdata` 目
录，其中包含了测试数据与输出结果。

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
> 这个方法的作用是解析字符串并为其中每一个路径构造一个 `ClassSearchPath` 的子类。
> 这样我们在 `ClassLoader` 中也不需要关心如何解析路径了。

对于类的创建，我们调用了 `JClass` 的构造方法。框架中只包含了一部分的构造过程，剩
下的我们会在 Lab 1.2 中实现它。

代码中需要你自己实现的部分我们已用 `UnimplementedError` 标识出来。你可以使用以下
命令来查找所有未实现的内容：

```
$ grep -irH UnimplementedError
```

我们在 `ClassLoader` 中定义了 `definedClass` 属性用于保存该加载器已加载的类，你
应该在完成加载之后将一个类记录到其中，以保证加载的类唯一。

> Java 8 与 Java 9+ 中加载系统类的差异
>
> 你可能注意到了 `getSystemSearchPaths` 中关于 JDK9+ 的注释和神秘的
> `ModuleSearchPath` 类。这是因为 Java 9 中引入了新的模块（module）系统，Java 8
> 上加载系统类的方式在这些版本上不能继续使用（即
> `System.getProperty("sun.boot.class.path")` 会返回 `null`）然而，vscode 等编辑
> 器的 Java 语言支持（[jdtls](https://github.com/eclipse/eclipse.jdt.ls)）要求
> Java 11 以上的版本。为了避免配置两个 java 环境的麻烦，我们在框架代码中加入了对
> Java 9+ 的支持。
>
> 为了能同时在 Java 8 和 Java 9+ 中编译，框架代码在 `ModuleSearchPath` 中使用了
> 反射在运行时访问 Java 8 中不存在的类。你不需要关心这些“丑陋的实现细节”。
>
> OJ 平台仍使用 Java 8 进行评测。Java 8 中系统类保存在一个 jar 文件当中，因此你
> 需要实现从 jar 文件加载类以通过相关测试。
>
> 框架代码对 Java 9+ 的支持未经过太多测试。因此如果你发现同一份代码在 Java 8 中
> 可以运行，而在 Java 9+ 中会报错，请报告给助教。

## 及时 commit 你的更改

~~我们会根据你的 Git 日志来评判你是否有抄袭的嫌疑。如果你把全部更改都放在一次
commit 中，那么我们就有很大可能怀疑你是 copy-paste 的。~~由于 OJ 系统的限制，检
查 git 历史碰到了一些困难...，但不用担心，我保证会另想办法对你们的代码查重。😝

即使我们不检查日志，你也应该养成及时 commit 的习惯。每次 commit 都会生成一份你
的仓库当前状态的副本，及时你不小心删除了某个文件也可以从这个副本中恢复。

我们还建议你将仓库 push 到 [GitHub](https://github.com) 或 [NJU
Git](https://git.nju.edu.cn) 上，这样即使你删除了电脑上的全部文件也可以在远程
仓库中找到备份。

<figure>
  <img src="{{ site.baseurl }}{% link assets/git-commit-in-case-of-fire.webp %}" />
  <figcaption>着火时需做的三件事</figcaption>
</figure>

你可以使用以下命令来完成 commit：

```
$ git add ${需要 commit 的文件}
$ git status
$ git commit -m "${描述本次 commit 的内容}"
```

> 字符串替换语法
>
> 我们约定，在文档中看到 `${这种文本}` 时，你需要将它替换成文本描述的内容。如将
> `${本仓库路径}` 替换为你进行开发的 Git 仓库在本地保存的路径，而将 `${OJ 仓库路径}`
> 替换为你从 OJ 克隆的仓库路径。
>
> 这种字符串替换的语法在 Unix Shell 中经常被使用。

---

Lab 1.1 到此结束，请将你的代码提交到 OJ 上。

由于 OJ 系统的作业仓库并非原本的仓库，你无法直接把本仓库的提交合并到 OJ 中。作为
workaround，请把你的代码直接拷贝到 OJ 仓库中提交（Windows 用户请自行把代码拷贝至
OJ 仓库中）：

```
$ cp -r ${本仓库路径}/src ${OJ 仓库路径}
$ cd ${OJ 仓库路径}
$ git add src
$ git commit -m "Upload code"
$ git push
```

**由于 OJ 仓库中的代码并非最新版本，请把原始仓库中的全部代码全部复制进 OJ 仓库再
提交。**
