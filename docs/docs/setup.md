---
sidebar_position: 1
title: 本地开发环境设置
---

[//]: # (Copyright 2024 Paion Data)

[//]: # (Licensed under the Apache License, Version 2.0 (the "License");)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

本节介绍开发 [Aristotle] 所需的一次性环境配置。

### 准备工作

在开始之前，请确保你的开发环境满足以下要求。本文以 `macOS` 为例。

### 1. 安装 Java 17

[Aristotle] 需要 `JDK 17` 版本。推荐使用 `Homebrew` 进行安装。

:::tip[国内用户提速]
如果您的 `brew` 下载速度很慢，可以考虑更换为国内镜像源，例如[中科大源](https://mirrors.ustc.edu.cn/help/brew.git.html)。
:::

```bash
brew update
brew install openjdk@17
```

安装命令执行后，终端会输出类似以下信息：

```bash
For the system Java wrappers to find this JDK, symlink it with
  sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

If you need to have openjdk@17 first in your PATH, run:
  echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

For compilers to find openjdk@17 you may need to set:
  export CPPFLAGS="-I/opt/homebrew/opt/openjdk@17/include"
```

**请务必根据你终端的实际输出来执行**上述 `sudo ln -sfn`、`echo 'export PATH=...'` 和 `export CPPFLAGS=` 命令，以确保 Java 环境配置正确。

如果看到类似以下输出，则说明 JDK 17 安装成功：

```bash
$ java --version
openjdk 17.0.10 2021-01-19
OpenJDK Runtime Environment (build 17.0.10+9)
OpenJDK 64-Bit Server VM (build 17.0.10+9, mixed mode)
```

### 2. 安装 Maven

项目使用 Maven 进行构建。同样，可使用 `Homebrew` 安装。

```bash
brew install maven
```

安装后，请务必检查 Maven 使用的 Java 版本。

```bash
$ mvn -v
Apache Maven 3.9.6 (...)
Maven home: /opt/homebrew/Cellar/maven/3.9.6/libexec
Java version: 17.0.10, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@17/17.0.10/libexec/openjdk.jdk/Contents/Home
...
```

:::tip[配置 JAVA_HOME]
如果 `mvn -v` 显示的 `Java version` 不是 17，你需要手动设置 `JAVA_HOME` 环境变量，使其指向你安装的 JDK 17。

首先，找到 JDK 17 的安装路径：
```bash
$ /usr/libexec/java_home -v 17
/opt/homebrew/Cellar/openjdk@17/17.0.10/libexec/openjdk.jdk/Contents/Home
```

然后，将 `JAVA_HOME` 添加到你的 Shell 配置文件中（如 `~/.zshrc` 或 `~/.bash_profile`）并使其生效：
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```
:::

### 3. 安装 Docker

[Aristotle] 包含[基于 Docker 的集成测试]，因此需要安装 Docker。
请根据[官方指引](https://docs.docker.com/desktop/install/mac-install/)完成安装。

### 4. 克隆代码仓库

一切就绪后，克隆项目源代码到本地：

```bash
git clone git@github.com:paion-data/aristotle.git
cd aristotle
```

[Aristotle]: https://github.com/paion-data/aristotle/

[基于 Docker 的集成测试]: https://github.com/paion-data/aristotle/blob/master/src/test/groovy/com/paiondata/aristotle/DockerComposeITSpec.groovy
