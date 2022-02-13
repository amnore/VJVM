---
parent: Lab0 开始之前
nav_order: 0
---

# Java 的主要面向对象特性

在本章中，我们将展示一些等价的 C 与 Java 程序，帮助你理解 Java 中主要的 OOP 特性。
对于 Java 的基本语法，请自行搜索“Java 入门”。

你可以编译运行下面展示的程序，也可以作出一些修改以便帮助理解程序执行的过程。

## 类与构造函数

如果抛开虚函数等 OOP 特性，类实际上与 C 中的结构体并无二致：每个类的实例（对象）
本质上是一块在内存中分配的内存，其中存储了成员变量的值；对象的引用与结构体的指针
作用相同。例如，对于以下 Java 程序：

```java
public class A {
  int a;
  double b;

  A() {
    a = 1;
    b = 2.0;
  }

  public static void main(String[] args) {
    A a = new A();
    System.out.printf("a=%d,b=%f\n", a.a, a.b);
  }
}
```

我们可以用这样的 C 代码来等价实现：

```c
#include <stdio.h>
#include <stdlib.h>

typedef struct A {
  int a;
  double b;
} A;

static A *new_A() {
  A *a = malloc(sizeof(A));
  a->a = 1;
  a->b = 2.0;
  return a;
}

int main() {
  A *a = new_A();
  printf("a=%d,b=%f\n", a->a, a->b);
}
```

## 成员函数

TODO

## 虚函数

TODO

## private 成员

TODO

## 继承

TODO
