---
nav_order: 2
parent: Lab1 类加载与解析
---

# 1.2 Parser

> 本章对应 JVM 规范内容：4.1 ~ 4.11。我们不会完全实现规范中所有的内容，请自
> 行跳过无关部分。

## Class 文件的结构

Class 文件存储了执行 Java 程序所需的（几乎）全部信息。我们在源代码文件中看到的全
部内容，都会以某种方式包含在其中。以一个简单的类为例：

```java
package test;

// 类的名称、所属的包被保存在一个名为常量池（constant pool）的表中。
// 更具体地，每个类名会被编码为 JVM 的内部表示，
// 常量池中会有两项，第一项是类名的字符串，第二项是指向字符串，
// 表明这是一个类。
public class A

  // 父类、接口、成员变量和方法引用到的类会以相同的方式保存在常量池中，
  // 并在相应的地方被引用。
  extends Object
  implements Cloneable {

  // class 文件中还有一个表用于存储每个类的成员，其中包含了类型、
  // 访问权限（access flags）等。
  // 每个成员的额外信息存储在一个属性（attribute）表中。
  private Object a;

  // 静态成员在 access flags 中有专门的一位标明。
  // 静态成员的初始值会保存在一个名为 ConstantValue 的属性中。
  public static String b = "1";

  // 对于非静态成员，其初始值不会显式保存在类中，
  // 而是在初始化方法（constructor）里面赋值。
  private int c = 3;

  // 类的方法保存在一个方法表中，方法的名称也会被转换成内部表示，
  // 包含了方法名和参数和返回值类型两部分信息。对于 constructor，
  // 方法的名称为特殊的 <init>。
  public A() {}

  public static void main(String[] args) {
    // 代码在经过编译后作为 Code 属性与对应的方法一起保存
    var a = new A();
  }

  // 静态初始化方法名称为 <clinit>
  static {}
}
```

经过编译后，我们可以使用 `javap` 命令查看这个 class 文件的内容：

```
$ javac A.java
$ javap -verbose A.class | less
```

关于 class 文件的具体结构，请阅读手册。

在 Lab 1.2 中，我们将对 class 文件的内容进行解析，并实现一个类似于 `javap` 的程
序，作为 Lab 2 的准备工作。

## 实验要求

我们要求你解析 class 文件，并输出到标准输出流中。如果这个类在 classpath 中找不到，
你的行为应和 Lab 1.1 相同；在找到需要的类后，你应该按照顺序每一行输出以下信息：

- 类名：以 `class name: ` 开始，后跟这个类的内部表示
- 小版本：以 `minor version: ` 开始，后以十进制输出
- 大版本：以 `major version: ` 开始，后以十进制输出
- 访问权限：以 `flags: ` 开始，后以十六进制输出，包含 `0x` 前缀
- 当前类：以 `this class: ` 开始，后跟该类的内部表示
- 父类：以 `super class: ` 开始，后跟父类的内部表示；无父类时名称应为空
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
  Package），我们不会检查你输出的常量类型和值，但你仍然应该为每个常量输出一行。

- 接口：以 `interfaces:` 开始，后按顺序为每一个接口输出一行接口名

- 成员变量：以 `fields:` 开始，后按顺序为每一个成员以 `${成员名}(${访问权限}):
  ${描述符}` 的格式输出

- 方法：以 `methods:` 开始，后按顺序为每一个方法以 `${方法名}(${访问权限}): ${描
  述符}` 的格式输出

我们在框架中给出了测试使用的比较方法和一个样例，你可以参考其中的内容。如果你发现测
试代码有什么 bug 或不合理的地方，请联系助教。

---

Lab 1 到此结束，请整理你的 Git 分支并提交到 OJ 上。

```
$ git checkout master
$ git merge lab1
$ git remote add lab1-oj ${OJ 提交地址}
$ git push lab1-oj master
```
