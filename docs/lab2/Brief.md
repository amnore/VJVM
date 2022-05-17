---
has_children: true
permalink: /lab2/
nav_order: 3
---

# Lab2 字节码解释执行

---

## 施工中

<img src="{{ site.baseurl }}{% link assets/under-construction.svg %}" />

本部分文档与框架代码尚未完成。你可以提前拉取文档完成作业，但我们不保证在发布之前
不会对相关部分作出破坏性的更改。

---

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
Lab 1 代码的冲突。你需要首先解决这些冲突。完成合并后，你可以运行 Lab 1 测试确认
原有代码仍然能继续运行。

> 不会 merge？
>
> 你可以在互联网上找到很多 Git 教程。如 [Oh My Git!](https://ohmygit.org/) 是一
> 个交互式学习 Git 的游戏；[Pro Git](https://git-scm.com/book/en/v2) 是一本关于
> Git 的书。你可以通过这些资源学习 Git，也可以自己寻找其它资源。

> 真的有必要学习 Git 吗？
>
> 是的。版本管理是软件开发过程中一个不可或缺的部分，而你现在能接触到的大部分项目
> 都是使用的 Git 作为版本管理工具。如果你连 merge 这种基本的操作的不会，以后上手
> 真正的项目必定是会吃苦头的。
