---
parent: Lab0 开始之前
nav_order: 2
---

# 0.3 Read The Friendly Code

在开始~~痛苦~~愉快的 JVM 之旅之前，请先允许我向你们介绍一下框架代码。

## 框架代码

```
$ tree src/main/java/vjvm
src/main/java/vjvm
├── utils
│   └── UnimplementedException.java
└── vm
    └── Main.java

2 directories, 2 files
```

此时的框架代码看上去十分寒酸：整个框架只有 `utils` 和 `vm` 共两个包四个类。在以
后的大作业中我们将逐渐把它变成一个完整的 JVM。

代码的入口是 `vm/Main.java` 中的 `Main` 类。你们可以使用 vim 查看它：

```
$ vim src/main/java/vjvm/vm/Main.java
```

框架代码使用了 [picocli](https://picocli.info/) 库来处理命令行的解析。代码中以
`@` 开始的部分被称为注解（Annotation），picocli 通过这种形式来指定命令行参数、解
析规则、参数的描述等。

除了命令行的三个类，我们还有一个异常类 `UnimplementedException` 用于标注未实现的
代码。由于我们当前什么都没实现，你在运行时会看到这样的信息：

```
$ ./gradlew jar  # Windows 用户请使用 gradlew.bat
$ java -jar build/libs/VJVM-0.0.1.jar dump java.lang.String
vjvm.utils.UnimplementedException
  at vjvm.vm.Dump.call(Main.java:58)
  at vjvm.vm.Dump.call(Main.java:47)
  ...
  at vjvm.vm.Main.main(Main.java:18)
```

这里面包含了发生异常的类型和现场的调用栈信息（stacktrace）。我们可以看到是
`Main.java` 的第 58 行抛出了这个异常。

## 测试框架

测试使用的代码和数据都在 `src/test` 目录中。对于 lab0，我们只有一个用例，Dummy：

```
$ cat src/test/java/lab0/Dummy.java
package lab0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Dummy {
  @Test
  void dummyTest() {
    assertEquals(0, 0);
  }
}
```

框架代码使用了 [JUnit](https://junit.org/junit5/docs/current/user-guide/) 进行测
试。你可以自行添加测试用例，但我们在运行 OJ 时会替换掉你的 `src/test` 目录。

要运行测试，请使用 `./gradlew test` 命令。

## 编译脚本

框架代码使用 gradle 执行编译、测试等工作。你可以在 `build.gradle` 找到项目配置：

```groovy
// ...

// 本项目使用 Java 8
sourceCompatibility = 1.8
targetCompatibility = 1.8

// ...

dependencies {
  // 项目使用的库
  implementation 'org.apache.commons:commons-lang3:3.12.0'
  implementation 'org.apache.commons:commons-text:1.9'
  implementation 'info.picocli:picocli:4.6.2'

  testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'

  annotationProcessor 'info.picocli:picocli-codegen:4.6.2'
}

jar {
  // 将整个项目打包到 build/libs/VJVM-0.0.1.jar
  // ...
}

test {
  // 运行测试时的配置
}
```

请勿对 `build.gradle` 作任何修改。在 OJ 上运行测试时我们会替换掉该文件，因此你的
修改可能会导致本地与 OJ 的编译配置不同，无法正确运行。

新手教程到此结束。请继续阅读下一章 Lab1.1 并完成作业。

> 整理你的 Git 分支
>
> 在每个 Lab 的最后，你都需要将本次作业的更改合并到 master 分支下。
>
> 请使用以下命令将 lab0 合并到 master：
>
> ```
> $ git checkout master
> $ git merge lab0
> ```
