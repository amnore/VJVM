---
nav_order: 0
parent: Lab1 类加载与解析
---

# ClassLoader

> 本章对应 JVM 规范内容：5.3.1、5.3.2

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

每个 class 想要执行它的职责首先得被加载到 JVM 中，这便是 ClassLoader 的工作。具
体而言，每个 ClassLoader 中保存了一组搜索 class 文件的路径。在请求加载一个
class 时，它会依次做这样几件事：

1. 查找自身是否已加载所需的 class，如果是则返回已加载的 class。
2. 从自己的加载路径中搜索这个 class，如果找到则利用 class 文件创建一个新
   class，或者
3. 请求另一个 ClassLoader 加载这个 class。

其中，步骤 2 和 3 可以按任何顺序执行，也可以只执行其中的一个。

## 双亲委托加载机制（Parent-First）

虽然 JVM 规范对加载 class 的顺序没做规定，但在 JDK 中默认使用了名为 parent-first
的策略：每个 loader（除 Bootstrap Loader 外）均有一个**亲代加载器（parent）**，
在搜索 class 时首先委托亲代进行搜索，找不到时才搜索自己的加载路径。于是，各个
ClassLoader 之间就形成了如下的委托关系：

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

> 修改框架代码
>
> 我们所有的测试都通过命令行进行（请参阅 test 目录中的样例）。因此，在维持命令行
> 接口不变的前提下，你可以任意修改框架代码。事实上，我们鼓励你按照自己的喜好重构
> 框架，并与我们交流你对代码设计的想法。
>
> 需要注意的是，我们在以后的 Lab 中会向框架加入新的代码。如果你对框架代码的接口
> 做了修改，在加入我们的代码时你可能需要进行相应的调整。不用害怕，这正是你学习
> `git merge`，`diff` 等工具的好机会。

在 Lab 1.1 中，我们将实现两个 ClassLoader：第一个为 Bootstrap Loader，负责从系统
JDK 中加载类；第二个为 User Loader，负责从命令行指定的 classpath 中搜索 class。
User Loader 以 parent-first 的方式委托给 Bootstrap Loader。

> 在真实的 JVM 中，User Loader 是作为虚拟机中的一个类来实现的。它从指定的路径中
> 查找 class 文件，并调用 JVM 提供的一个方法由文件创建类。然而，这种实现依赖于
> native 方法等复杂内容，因此我们在此作了简化，由 JVM 来实现 ClassLoader。

在框架的 ClassLoader.java 中，我们已为你准备好了加载类的接口：`public JClass
loadClass(String descriptor)`。该方法接受一个字符串，返回加载的类。在 Lab 1.2 中，
你将解析 class 文件并填入返回的类中。需要注意的是，这里传入的不是 class 名称，而
是它的描述符（descriptor）。如 `java.lang.String` 类对应 `Ljava/lang/String;`。
对于 descriptor 我们将在 Lab 1.2 中再次提及。

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

   Java Module 是 Java 9 中新引入的一种打包机制，提高了 JDK 的模块化程度。每个
   module 文件本质上是一个 zip 文件加上 4 bytes 文件头。我们只会在 Bootstrap
   Loader 中加载 system modules，你可以使用 `ModuleFinder.ofSystem()` 获取其加载
   路径。

这三种加载路径事实上具有同样的接口（interface）：给定一个 class，从中搜索对应的
文件。我们将这个接口抽象成了 `ClassSearchPath` 类。

Bootstrap Loader 仅加载 system modules，User Loader 从命令行的 `-cp` 参数中读取
加载路径并加载相应的类。该参数可能包含一个或多个加载路径，每两个之间以路径分隔符
（path separator）隔开。对于类 Unix 环境，separator 为 `:`，Windows 下则是 `;`。
你可以使用`System.getProperty("path.separator")` 获取路径分隔符。

在成功找到所需类时，我们要求你的命令返回 0；找不到时需返回非 0 值。你可以利用标
准错误流 `System.err` 输出任意调试信息，但请勿使用标准输出流 `System.out`。我们
在之后的 Lab 中会将其中的信息作为测试输出。
