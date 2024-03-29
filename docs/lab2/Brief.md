---
has_children: true
permalink: /lab2/
nav_order: 3
---

# Lab2 字节码解释执行

在 Lab 1 中，我们实现了类加载与解析。接下来，我们将在此基础上解释执行 Java 字节码。

在开始之前，请拉取 Lab 2 框架代码（如果你在 Lab 1 末尾已经把更改合并至 master 分
支，则可以跳过第 2 与第 3 步）：

```
$ git pull origin
$ git checkout master
$ git merge lab1
$ git checkout lab2
$ git merge master
```

在 pull 时，你可能会遇到 `Exiting because of an unresolved conflict` 的报错。这
是因为 Lab 1 框架在发布后对 `Constant.java` 的注释作了一处修改。你需要按照 git
的提示解决冲突。同样地，在将 master 合并至 lab2 的过程中你可能会遇到新增代码与
Lab 1 代码的冲突。你需要首先解决这些冲突，保留 Lab 2 新加的代码和 Lab 1 中你自
己编写的代码。完成合并后，你可以运行 Lab 1 测试确认原有代码仍然能继续运行。

> 不会 merge？
>
> 你可以在互联网上找到很多 Git 教程。如 [Oh My Git!](https://ohmygit.org/) 是一
> 个交互式学习 Git 的游戏；[Pro Git](https://git-scm.com/book/en/v2) 是一本关于
> Git 的书。你可以通过这些资源学习 Git，也可以自己寻找其它资源。
>
> <figure>
> <img src="{{ site.baseurl }}{% link assets/oh-my-git.jpg %}" />
> <figcaption>是的，你甚至可以通过游戏来学 Git！</figcaption>
> </figure>

> 真的有必要学习 Git 吗？
>
> 是的。版本管理是软件开发过程中一个不可或缺的部分，而你现在能接触到的大部分项目
> 都是使用的 Git 作为版本管理工具。如果你连 merge 这种基本的操作的不会，以后必然
> 是没法上手真正的项目开发的。
