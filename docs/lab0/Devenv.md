---
parent: Lab0 开始之前
nav_order: 1
---

# 开发环境配置

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
；我们在编写文档时给出的一些例子会假设你在 Linux 命令行环境中；编写框架代码和 OJ
测试都主要在 Linux 环境下进行，不保证没有平台环境差异导致的 bug。

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

网上有大量的 GNU/Linux 入门教程，如有需要请自行搜索。这里我们给出 [ICS
PA0](https://nju-projectn.github.io/ics-pa-gitbook/ics2021/PA0.html) 的相关链接。

> 请使用英文版本 GNU/Linux
>
> 无论是在 GNU/Linux 中还是在 Windows 中，许多软件都没有高质量的中文翻译。对于开
> 发环境这个问题更明显：如果你尝试过使用 GCC 在中文环境下编译程序，你应该知道我
> 说的是什么了。同时，网络上大量高质量的资料都只有英文版本，只看中文会极大地限制
> 你能搜集到的资源。
>
> 所以，好好学英语吧。以后无论是做科研还是做项目，这都是一项必不可少的技能。

## 安装 Java 工具链

[gradle](https://gradle.org/)（以及 [maven](https://maven.apache.org/)）是用于构
建 Java 项目的工具。由于 gradle 有更友好的构建脚本语法和命令行，我们选择它作为大
作业的构建工具。框架源码树中已包含了 gradle，请在根目录使用 `./gradlew` 运行。
（Windows 用户请使用 `gradlew.bat`）

框架代码基于 Java 17 进行开发，Windows 用户请自行搜索对应版本 JDK 下载。对于
Ubuntu 用户，你们可以使用以下命令进行安装：

```
# apt install openjdk-17-jdk
```

> 对于 Shell 命令，我们约定以‘$’开始时为普通用户权限，以‘#’开始时为 root 权限。
> 你可以使用 sudo 来以 root 权限执行命令。

> 如非必要，不要使用 root 用户！
>
> root 用户具有整个系统的最高权限，可以任意更改、删除任何文件。不适当操作极有可
> 能导致系统无法启动。在完成大作业时请使用普通用户。

## 选择称手的编辑器

如果你已经是熟练的 Vim/Emacs 用户，请忽略这一节。

对于新用户，我们推荐你使用 [VSCode](https://code.visualstudio.com/)。你可以在
“Ubuntu Software”中下载到它。同时，我们推荐使用 Vim 插件以获得更高效的编辑体验。

如果你更偏好 IDE，我们推荐 IDEA Community。同样地，我们推荐使用 IDEAVim 插件。

本项目使用 lombok 库，需要特殊设置才能在编辑器中正常使用自动补全、错误检查等功能。
请自行查找设置方法。

## 获取框架代码

使用以下命令获取框架代码：

```
$ git clone https://git.nju.edu.cn/czg/VJVM-public jvm-2022
$ cd jvm-2022
$ git checkout lab0
```

进入框架目录后，请设置你的学号和邮箱：

```
$ git config user.name '211250001-Zhang San'
$ git config user.email '211250001@smail.nju.edu.cn'
```
