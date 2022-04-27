---
parent: Lab0 开始之前
nav_order: 1
---

# 0.2 开发环境配置

所谓“工欲善其事，必先利其器”，各位在做 OJ 题的时候用网页编辑器甚至记事本都可以完
成，但对于有一定规模的项目就得先配置开发环境才能更高效地开发。

> 环境差异导致的 bug
>
> czg 大二写 OSLab 时曾遇到过这样一件事：作为 OS 的一部分，czg 需要实现一个文件
> 系统。他编写了 [mkfs](https://linux.die.net/man/8/mkfs) 工具格式化镜像文件
> 并把目录中用于测试的文件复制到新建的文件系统根目录下。
>
> 为了完成读取文件的操作，czg 使用了
> [readdir](https://linux.die.net/man/3/readdir) 函数。这个函数不保证读取文件的
> 顺序，但在 czg 使用的系统中，调用这个函数读到的前两项必定是 '.'（当前目录）和
> '..'（父目录）。
>
> 没有认真 RTFM 的 czg 很高兴地硬编码跳过了每个目录的前两项文件。于是在提交上OJ
> 之后，mkfs 就因为没有跳过当前目录而无限递归爆栈了。这个 bug 卡了 czg 整整一
> 个星期，并且他最后是找老师要了后台的日志，并开了一个虚拟机进行测试才找出问题所
> 在。
>
> <figure>
>   <figcaption>czg 当时的提交记录（🤡竟是我自己！）</figcaption>
>   <img src="{{ site.baseurl }}{% link assets/oslab3.jpg %}" />
> </figure>

## 安装 GNU/Linux

作为一个“Build once, run everywhere”的语言，Java 开发理论上可以在任何平台上进行。
然而，我们仍然推荐你使用 GNU/Linux 进行开发：[GNU/Linux 中自带的大量命令行工具可
以极大地提升你的开发效
率
](https://nju-projectn.github.io/ics-pa-gitbook/ics2021/0.5.html#why-gnulinux-and-how-to)
。我们在编写文档时给出的一些例子会假设你在 Linux 命令行环境中；编写框架代码和 OJ
测试也都 Linux 环境下进行。

当然，如果你仍然希望使用 Windows，请跳过这一节内容。对于 MacOS 用户，你们也可以
跳过这一节内容，但我们推荐你首先熟悉命令行的使用。

安装 Linux 环境主要有三种方法：
[WSL](https://docs.microsoft.com/en-us/windows/wsl/install)、虚拟机（可以使用
[Virtual Box](https://www.virtualbox.org/) 或 [VMWare
Player](https://www.vmware.com/products/workstation-player/workstation-player-evaluation.html)
，请自行查找使用方法）、真机（请事先备份数据）。对于虚拟机与真机，你可以下载
[Ubuntu
21.10](https://mirrors.nju.edu.cn/ubuntu-releases/impish/ubuntu-21.10-desktop-amd64.iso)
镜像进行安装。

网上有大量的 GNU/Linux 入门教程，如有需要请自行搜索。这里我们推荐 MIT 的 [The
Missing Semester of Your CS Education](https://missing.csail.mit.edu/) 的相关链
接。

大作业教程中有许多 Shell 命令，你可以在终端模拟器（Terminal）中执行它们。Ubuntu
已自带终端模拟器，安装完成后可以在应用中找到它。

## 选择称手的编辑器

对于新用户，我们推荐你使用 [VSCode](https://code.visualstudio.com/)。同时，我们
推荐使用 Vim 插件以获得更高效的编辑体验。

如果你更偏好 IDE，我们推荐 IDEA Community。同样地，我们推荐使用 IDEAVim 插件。

本项目使用 lombok 库，需要特殊设置才能在编辑器中正常使用自动补全、错误检查等功能。
请自行查找设置方法。

## 安装 Java 工具链

[gradle](https://gradle.org/)（以及 [maven](https://maven.apache.org/)）是用于构
建 Java 项目的工具。由于 gradle 有更友好的构建脚本语法和命令行，我们选择它作为大
作业的构建工具。框架仓库中已包含了 gradle，请在根目录使用 `./gradlew` 运行。
（Windows 用户请使用 `gradlew.bat`）

框架代码基于 Java 8 进行开发，Windows 用户请自行搜索对应版本 JDK 下载。对于
Ubuntu 用户，你们可以使用以下命令进行安装：

```
$ sudo apt install openjdk-8-jdk
```

VSCode 的 Java 语言支持需要 Java 11 以上的版本，因此你需要把以上安装的包替换成
`openjdk-11-jdk`。

## 获取框架代码

使用以下命令获取框架代码：

```
$ git clone https://git.nju.edu.cn/czg/VJVM-public jvm-2022
$ cd jvm-2022
$ git checkout lab0
```

> 由于 OJ 系统的功能限制，你使用以上命令获得的仓库与从 OJ 作业地址中 clone 的并
> 非同一个仓库。在大作业中，你的每次作业也需要以之前的代码为基础，同时随着大作业
> 的进行，我们也会在这个仓库中给出更多的框架代码。
> **因此请全程使用本仓库进行开发。**在 lab1.1 的最后，我们会给出提交的方法。

进入框架目录后，请设置你的学号和邮箱：

```
$ git config user.name '211250001-Zhang San'
$ git config user.email '211250001@smail.nju.edu.cn'
```

