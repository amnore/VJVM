---
parent: Lab0 开始之前
nav_order: 0
---

# Java 的主要面向对象特性

在本章中，我们将展示一些等价的 C 与 Java 程序，帮助你理解 Java 中主要的 OOP 特性。
对于 Java 的基本语法，请自行搜索“Java 入门”。

我们在这一章中只展示最基本的面向对象特性。如果你对更多面向对象特性的实现方法感
兴趣请阅读 [Object-Oriented Programming With
ANSI-C](https://www.cs.rit.edu/~ats/books/ooc.pdf)。

你可以自行编译运行下面展示的程序，也可以作出一些修改以便帮助理解程序执行的过程。

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
  free(a);

  return 0;
}
```

## 成员函数

Java 中的静态成员函数与 C 中普通函数并无差异，而成员函数事实上是在参数中插入了一
个 `this`，在调用时将对象本身传递给这个函数。因此，对于这样一个 Java 程序：

```java
public class A {
  int a = 1;

  void print(int b) {
    System.out.printf("a=%d,b=%d\n", a, b);
  }

  public static void main(String[] args) {
    A a = new A();
    a.print(2);
  }
}
```

我们可以改写成以下 C 代码：

```c
#include <stdio.h>
#include <stdlib.h>

typedef struct A {
  int a;
} A;

static A *new_A() {
  A *a = malloc(sizeof(A));
  a->a = 1;
}

static void A_print(A *this, int b) {
  printf("a=%d,b=%d\n", this->a, b);
}

int main() {
  A *a = new_A();
  A_print(a, 2);
  free(a);

  return 0;
}
```

## 继承

子类继承了父类的所有成员。这实际上就是将父类的成员复制到子类中。因此，我们可以通
过在结构体中放置一个父类来实现继承：

```java
class A {
  int a;

  A() {
    a = 1;
  }

  public static void main(String[] args) {
    A a = new A();
    System.out.printf("a=%d\n", a.a);
    B b = new B();
    System.out.printf("a=%d,b=%d\n", b.a, b.b);
    a = b;
    System.out.printf("a=%d\n", a.a);
  }
}

class B extends A {
  int b;

  B() {
    a = 2;
    b = 3;
  }
}
```

与以下 C 程序等价。注意由于 `a` 是 `B` 的第一个成员，一个 `B` 对象的地址与其中
`a` 的地址相同。因此我们可以安全地把 `B` 的指针转换为 `A` 的指针：

```c
#include <stdio.h>
#include <stdlib.h>

typedef struct A {
  int a;
} A;

typedef struct B {
  A a;
  int b;
} B;

static A *new_A() {
  A *a = malloc(sizeof(A));
  a->a = 1;
  return a;
}

static B *new_B() {
  B *b = malloc(sizeof(B));
  b->a.a = 2;
  b->b = 3;
}

int main() {
  A *a = new_A();
  printf("a=%d\n", a->a);
  B *b = new_B();
  printf("a=%d,b=%d\n", b->a.a, b->b);

  free(a);
  a = (A*)b;
  printf("a=%d\n", a->a);
  free(a);

  return 0;
}
```

C++ 中还有多继承、虚继承等特性，这些情况下的实现更为复杂，我们就不在此演示了。

## 虚函数

虚函数使得父类和子类的“同一个”函数可以有不同的行为。我们可以利用函数指针实现虚函
数：

```java
class A {
  void print() {
    System.out.printf("class A\n");
  }

  public static void main(String[] args) {
    A a = new A();
    a.print();
    a = new B();
    a.print();
  }
}

class B extends A {
  @Override
  void print() {
    System.out.printf("class B\n");
  }
}
```

等价于：

```c
#include <stdio.h>
#include <stdlib.h>

typedef struct A {
  void (*print)();
} A;

typedef struct B {
  A a;
} B;

static void A_print() {
  printf("class A\n");
}

static void B_print() {
  printf("class B\n");
}

static A *new_A() {
  A *a = malloc(sizeof(A));
  a->print = A_print;
}

static B *new_B() {
  B *b = malloc(sizeof(A));
  b->a.print = B_print;
}

int main() {
  A *a = new_A();
  a->print();
  free(a);

  a = (A*)new_B();
  a->print();
  free(a);

  return 0;
}
```

事实上，C++ 编译器在实现虚函数时并不会直接把函数指针保存在类中。由于同一个类的所
有对象调用的函数是相同的，编译器会每个类生成一个虚函数表（vtable）用于保存所有虚
函数，并每个对象中保存一个指向虚函数表的指针。在一个类有多个虚函数时，这种办法可
以节省每个对象占用的空间。
