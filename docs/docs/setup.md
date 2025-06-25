---
sidebar_position: 1
title: 快速开始
---

[//]: # (Copyright 2024 Paion Data)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

本节讨论开发 [Aristotle] 所需的一次性设置。

准备本地开发环境
-------------

### 安装 Java 和 Maven（适用于 Mac）

```bash
brew update
brew install openjdk@17
```

在最后一次命令执行完成后，终端可能会输出类似如下信息：

```bash
For the system Java wrappers to find this JDK, symlink it with
  sudo ln -sfn ...openjdk@17/libexec/openjdk.jdk .../JavaVirtualMachines/openjdk-17.jdk

openjdk@17 is keg-only, which means it was not symlinked into /usr/local,
because this is an alternate version of another formula.

If you need to have openjdk@17 first in your PATH, run:
  echo 'export PATH=".../openjdk@17/bin:$PATH"' >> .../.bash_profile

For compilers to find openjdk@17 you may need to set:
  export CPPFLAGS="-I.../openjdk@17/include"
```

请确保执行上述中的 `sudo ln -sfn`, `echo 'export PATH=...`, 和 `export CPPFLAGS=` 命令。

:::tip

Maven 使用的是一个独立的 JDK 版本，可以通过 `mvn -v`. 查看。如果它不是 JDK 17，我们需要使用 [JAVA_HOME](https://stackoverflow.com/a/2503679) 让 Maven 指向我们安装的 JDK 17:

```bash
$ /usr/libexec/java_home
/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

$ export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

:::

在输入带有版本标志的命令后，如果我们看到类似以下的内容，则说明安装成功：

```bash
$ java --version
openjdk 17.0.10 2021-01-19
OpenJDK Runtime Environment (build 17.0.10+9)
OpenJDK 64-Bit Server VM (build 17.0.10+9, mixed mode)
```

### 安装 Docker Engine

<!-- markdown-link-check-disable -->
[Aristotle] 包含基于 [Docker 的集成测试];
可以通过访问其 [official instructions](https://docs.docker.com/desktop/install/mac-install/) 来安装 Docker。
<!-- markdown-link-check-enable -->

获取源代码
-------------------

```bash
git clone git@github.com:paion-data/aristotle.git
cd aristotle
```

[Aristotle]: https://github.com/paion-data/aristotle/

[Docker 的集成测试]: https://github.com/paion-data/aristotle/blob/master/src/test/groovy/com/paiondata/aristotle/DockerComposeITSpec.groovy
