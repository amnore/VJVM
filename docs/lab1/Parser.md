---
nav_order: 2
parent: Lab1 类加载与解析
---

# 1.2 Parser

> 本章对应 JVM 规范内容：4.1 ~ 4.11。我们不会完全实现规范中所有的内容，请自
> 行跳过无关部分。

Class 文件存储了执行 Java 程序所需的（几乎）全部信息。我们在源代码文件中看到的全
部内容，都会以某种方式包含在其中。在 Lab 1.2 中，我们将对 class 文件的内容进行解
析，作为 Lab 2 的准备工作。

## 实验要求

我们要求你解析在上次作业中找到的 class 文件，并输出到 `System.out` 中。如果这个
类在 classpath 中找不到，你的行为应和 Lab 1.1 相同；在找到需要的类后，你应该按照
顺序每一行输出以下信息：

- 类名：以 `class name: ` 开始，后跟这个类的二进制名称（binary bame）
- 小版本：以 `minor version: ` 开始，后以十进制输出
- 大版本：以 `major version: ` 开始，后以十进制输出
- 访问权限：以 `flags: ` 开始，后以十六进制输出，包含 `0x` 前缀
- 当前类：以 `this class: ` 开始，后跟该类的二进制名称
- 父类：以 `super class: ` 开始，后跟父类的二进制名称；无父类时名称应为空
- 常量池：以 `constant pool:` 开始，后按顺序为每一个常量输出一行

  对于每一行，以 `#${索引} = ${常量类型}: ${常量值}` 的形式输出。其中，常量类型
  为 JVM 规范表 4.4 中的名称去除 `CONSTANT_` 前缀；对于值，我们要求你解析以下形
  式的常量：

    - Class：输出其名称
    - Fieldref、Methodref、InterfaceMethodref：以 `${类名}.${成员名}:${方法/成员
      描述符}` 的形式输出
    - String、Utf8：以 `"${字符串}"` 的形式输出，其中字符串应使用
      `org.apache.commons.text.StringEscapeUtils.escapeJava` 进行转义。
    - Integer、Long：以默认格式输出
    - Float、Double：为了保证不丢失精度，我们要求你使用 16 进制浮点数输出。
    - NameAndType：以 `${名称}:${描述符}` 的形式输出

  对于其它类型的常量（MethodHandle、MethodType、Dynamic、InvokeDynamic、Module、
  Package），你可以以任何方式处理（比如参考实现使用 `UnknownConstant` 代表所有这
  些类型的常量。唯一的要求是你应该为每个常量输出一行。

- 接口：以 `interfaces:` 开始，后按顺序为每一个接口输出一行接口名

- 成员变量：以 `fields:` 开始，后按顺序为每一个成员以 `${成员名}(${访问权限}):
  ${描述符}` 的格式输出

- 方法：以 `methods:` 开始，后按顺序为每一个方法以 `${方法名}(${访问权限}): ${描
  述符}` 的格式输出

我们在框架中给出了测试使用的比较方法和一个样例，你可以参考其中的内容。如果你发现测
试代码有什么 bug 或不合理的地方，请联系助教。

## JVM 手册导读

在本次 Lab 中，你第一次需要与 [THE FRIENDLY
MANUAL™](https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf) 打交道。作为引
导，我们将解释手册相关部份的内容，并给一些读手册的建议。

> 有中文手册吗？
>
> 答案很简单：没有。你在网上可能找到 JVM 手册的中文翻译，但你并不知道其中的内容
> 有没有严重的错误。大作业的评测都将以原本手册为准。
>
> 如果你无法摆脱对英文资料的恐惧感，那么你的水平也将被永远限制在从 CSDN 抄代码的
> 层次。如果你在阅读手册时有理解上的困难，唯一的办法是静下心来一边查单词一边读。
> 最开始时你可能会很痛苦（谁不是呢？😂），但熟练之后你就会觉得这其实也没什么大不
> 了的。

### 我应该如何阅读手册？

首先，手册不是小说。这意味着你不需要把它从头看到尾。拿到一部新的手册时，正确的阅
读顺序应该是这样的：

1. 快速过一遍目录和第一节内容。第一节中通常介绍了整个手册的组织结构和词汇的使用
   约定。你不需要把它们全部记住，因为以后不记得时可以随时回来查阅。
2. 找到你需要的部分进行阅读。手册的每一部分通常也是总-分结构，这意味着你通常也应
   首先去看每个部分的第一小节，然后在剩余的内容中查找自己需要的。
3. 在第一遍读手册时，你不应追求记住全部的细节，而是尽可能对其内容有一个总体上的
   理解，这样以后需要时就可以快速定位到相关章节。事实上，手册中的大部分内容都是
   不值得去记忆的——你随时可以查阅它们。
4. 在读完第一遍之后，你就可以开始写代码了。此时你最好把编辑器和手册并排打开，减
   少代码写到一半需要查阅手册的“上下文切换”。如果你没有 iPad 作为第二块屏幕，并
   且电脑的屏幕也不够宽，你可以使用使用虚拟桌面达到相似的效果——记住切换桌面的快
   捷键会让你写代码的流程顺畅许多。

<figure>
  <img src="{{ site.baseurl }}{% link assets/x86-manual.png %}" />
  <figcaption>再次强调，手册不是小说！JVM 的手册“只有”600 多页，或许你还可以读完
  它。但当你拿到 5000 页的 x86 手册时，你真的还准备一个字一个字去读吗？</figcaption>
</figure>

对于本次 Lab 而言，你需要阅读的部分主要是 Chapter 4. The class File Format。为了
降低本次作业的难度，我们接下来会解释这一节中最重要的内容。

### Class 文件的结构

第四章一开始便以类似于 C 语言结构体的形式给出了 class 文件的整体结构。我们将它摘
抄如下：

```c
struct ClassFile {
  u4              magic;
  u2              minor_version;
  u2              major_version;
  u2              constant_pool_count;
  cp_info         constant_pool[constant_pool_count - 1]; // 常量池
  u2              access_flags;
  u2              this_class;
  u2              super_class;
  u2              interfaces_count;
  u2              interfaces[interfaces_count]; // 类实现的接口
  u2              fields_count;
  field_info      fields[fields_count]; // 类的成员变量
  u2              methods_count;
  method_info     methods[methods_count]; // 类的方法
  u2              attributes_count;
  attribute_info  attributes[attributes_count];
};
```

同 C 一样，每一行的前一列是类型，后一列是名称。例如，`u4 magic` 代表 `magic` 为
32 位无符号整形。在一个 class 文件中，最重要的四个结构是常量池（Constant Pool）、
成员变量（Field）、方法（Method）和属性（Attribute）。

继续往下看，在 4.4 节中有常量池中每一项的结构：

```c
struct cp_info {
  u1  tag; // 常量类型
  u1  info[]; // 常量数据
};
```

需要注意的是，这里的 `info` 成员的长度是可变的。比如，对于 `CONSTANT_Class_info`：

```c
struct CONSTANT_Class_info {
  u1  tag;
  u2  name_index;
};
```

这里的 `info` 就被替换成了两个字节的索引，指向常量池中代表这个类名称的项。

JVM 规范中定义了许多的常量类型，但你需要解析的只有作业中要求的几种。对于其它类型，
你可以直接跳过它们。

需要注意的一点是，`CONSTANT_Long_info` 与 `CONSTANT_Double_info` 占用了常量值的
连续的两项。即当第 `n` 项为 `Constant_Long_info` 时，下一项的标号是 `n + 2`。

类的成员变量和方法在 class 文件中都有相似的结构：

```c
struct field_info {
  u2              access_flags;
  u2              name_index;
  u2              descriptor_index;
  u2              attributes_count;
  attribute_info  attributes[attributes_count];
};
```

其中的 `name_index` 和 `descriptor_index` 都指向常量池的内容。

最后是属性。属性在类、成员变量和方法的结构中出现，一个属性的通用结构如下：

```c
struct attribute_info {
  u2  attribute_name_index;
  u4  attribute_length;
  u1  info[attribute_length];
};
```

属性中保存了许多内容，如 `static final` 变量的值、方法的代码、异常处理所需的信息
等。同样地，在本次作业中你可以把它们全部忽略。我们在以后需要时才会进行解析。

关于 class 文件的更多细节，我们不会在此赘述。你需要自己去阅读手册，并完成各个结
构的解析。

### Class 文件中的名称

一个类中有类名、方法名、描述符等多种名称，我们将它们总结如下：

1. 二进制名称（binary name）：二进制名称是将 `${包名}.${类名}` 中的 `.` 替换成
   `/` 得到，每个类都有唯一的二进制名称。对于嵌套类，其名称为 `${外层类名}$${内
   层类名}`。
2. 方法、成员变量名称：与源代码中的名称相同。构造函数与静态构造函数的名称分别为
   `<init>` 与 `<clinit>`。
3. 描述符（descriptor）：描述符是一个类型的名称。Java 中不同种类的描述符如下：
  1. 基本类型：基本类型的描述符为单个大写字母，如 `char` 类型对应 `C`。
  2. 数组类型：数组类型的描述符为元素类型前跟 `[`。n 维数组便有 n 个 `[`。
  3. 对象类型：对象类型的描述符为 `L${对象二进制名称};`。
  4. 方法：方法的描述符为 `(${第一个参数描述符}${第二个参数描述符}...)${返回值类
     型描述符}`，如 `Object m(int i, double d)` 的描述符为
     `(ID)Ljava/lang/Object;`

### 查看 class 文件的内容

“Class 文件好复杂呀！有没有办法看到它的内容呢？”你能想到的事情，必定有人已经做过
了。我们可以使用 JDK 自带的 `javap` 命令来查看 class 文件的内容，以下面这个类为
例：

```java
package test;

public class A extends Object implements Cloneable {
  private Object a;
  public static String b = "1";
  private int c = 3;
  public A() {}

  public static void main(String[] args) {
    var a = new A();
  }

  static {}
}
```

将这个类保存到 `A.java` 中，然后命令行执行以下两条命令：

```
$ javac A.java
$ javap -verbose A.class | less
```

你就可以看到这个类都有哪些内容了。

实际上，我们在本次作业中实现的便是一个简化版 `javap` 程序，因此你在编写代码时可
以以它为参考。

---

Lab 1 到此结束，请整理你的 Git 分支并提交到 OJ 上。

```
$ git checkout master
$ git merge lab1
```

提交方式与 Lab1.1 相同。
