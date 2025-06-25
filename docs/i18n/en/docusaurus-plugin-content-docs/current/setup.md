---
sidebar_position: 1
title: Local Development Setup
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

This section covers the one-time setup required to develop [Aristotle]. This guide assumes you are on `macOS`.

### 1. Install Java 17

[Aristotle] requires JDK 17. We recommend installing it via [Homebrew](https://brew.sh/).

```bash
brew update
brew install openjdk@17
```

After the command finishes, it will print instructions for setting up your environment. It will look something like this:

```bash
For the system Java wrappers to find this JDK, symlink it with
  sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

If you need to have openjdk@17 first in your PATH, run:
  echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

For compilers to find openjdk@17 you may need to set:
  export CPPFLAGS="-I/opt/homebrew/opt/openjdk@17/include"
```

**Make sure to execute the commands** (`sudo ln -sfn`, `echo 'export PATH...'`, and `export CPPFLAGS=`) provided in your terminal output to configure Java correctly.

Verify the installation by running:

```bash
$ java --version
openjdk 17.0.10 2021-01-19
OpenJDK Runtime Environment (build 17.0.10+9)
OpenJDK 64-Bit Server VM (build 17.0.10+9, mixed mode)
```

### 2. Install Maven

The project is built using Maven. You can also install it with Homebrew.

```bash
brew install maven
```

After installation, verify that Maven is using the correct JDK version.

```bash
$ mvn -v
Apache Maven 3.9.6 (...)
Maven home: /opt/homebrew/Cellar/maven/3.9.6/libexec
Java version: 17.0.10, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@17/17.0.10/libexec/openjdk.jdk/Contents/Home
...
```

:::tip[Setting JAVA_HOME]
If `mvn -v` shows a Java version other than 17, you need to set the `JAVA_HOME` environment variable to point to your JDK 17 installation.

First, find the path to JDK 17:
```bash
$ /usr/libexec/java_home -v 17
/opt/homebrew/Cellar/openjdk@17/17.0.10/libexec/openjdk.jdk/Contents/Home
```

Then, add `JAVA_HOME` to your shell's config file (e.g., `~/.zshrc` or `~/.bash_profile`) and apply it:
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```
:::

### 3. Install Docker

[Aristotle] includes [Docker-based integration tests], so Docker is required.
Please follow the [official instructions](https://docs.docker.com/desktop/install/mac-install/) to install it.

### 4. Clone the Repository

Once everything is set up, clone the source code to your local machine:

```bash
git clone git@github.com:Doom9527/aristotle-webservice.git
cd aristotle-webservice
```

[Aristotle]: https://github.com/paion-data/aristotle/

[Docker-based integration tests]: https://github.com/paion-data/aristotle/blob/master/src/test/groovy/com/paiondata/aristotle/DockerComposeITSpec.groovy
