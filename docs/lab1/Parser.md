---
nav_order: 2
parent: Lab1 类加载与解析
---

# Parser

> 本章对应 JVM 规范内容：4.1 ~ 4.11，我们不会完全实现规范中所有要求的内容，请自
> 行跳过。

## Class 文件的结构

Class 文件存储了执行 Java 程序所需的（几乎）全部信息。理所当然地，我们在源代码文
件中看到的全部内容，都会以某种方式包含在其中。我们以一个简单的类为例：

```java
package test;

// 类的名称、所属的包被保存在一个名为常量池（constant pool）的表中。
// 更具体地，每个类名会被编码为 JVM 的内部表示（即 Lab 1.1 中我们传给 loadClass 方
// 法的形式），常量池中会有两项，第一项是类名的字符串，第二项是指向字符串，
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

事实上，我们可以使用 `javap` 命令查看这个 class 文件的内容：

```
$ javac A.java
$ javap -verbose A.class | less
```

在 Lab 1.2 中，我们将对 class 文件的内容进行解析，并实现一个类似于 `javap` 的程
序，作为 Lab 2 的准备工作。

## 实验要求

我们要求你解析 class 文件，并输出到标准输出流中。具体的输出格式要求如下：
