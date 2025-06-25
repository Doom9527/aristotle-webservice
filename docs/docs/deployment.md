---
sidebar_position: 3
title: 部署指南
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

本节介绍如何在生产环境部署 [Aristotle]。

生产部署准备
----------------------

:::note

假设你使用的是 Ubuntu 22.04+ 服务器进行部署。

:::

### 安装 JDK 17

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

如果你在输入以下命令后看到类似输出，说明安装成功：

```bash
$ java -version
openjdk version "17.0.11" 2024-04-16
OpenJDK Runtime Environment (build 17.0.11+9-Ubuntu-120.04.2)
OpenJDK 64-Bit Server VM (build 17.0.11+9-Ubuntu-120.04.2, mixed mode, sharing)
```

### 安装 Maven

```bash
sudo apt install maven
```

如果你在输入以下命令后看到类似输出，说明安装成功：

```bash
$ mvn -version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.11, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-182-generic", arch: "amd64", family: "unix"
```

在上述示例中，Maven 已经使用了正确的 JDK，因此无需额外设置 JAVA_HOME。但如果你希望显式指定 JAVA_HOME，或在有多个 JDK 安装时确保 Maven 始终使用 JDK 17，可以在 shell 配置文件（如 .bashrc、.zshrc 或 .profile）中添加如下内容：

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

打包 Aristotle
----------------------

```bash
git clone https://github.com/paion-data/aristotle.git
cd aristotle

export NEO4J_URI=YOUR_NEO4J_URI
export NEO4J_USERNAME=YOUR_NEO4J_USERNAME
export NEO4J_PASSWORD=YOUR_NEO4J_PASSWORD
export NEO4J_DATABASE=YOUR_NEO4J_DATABASE

mvn clean package -Dmaven.test.skip=true
```

[Aristotle] 基于 [Springboot](https://spring.io/projects/spring-boot) 构建，内置 Web 容器，使用 Maven 打包为 jar 文件。

启动 Aristotle
----------------------

```bash
java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

Web 服务将运行在 **8080** 端口。

### 获取 OpenAPI 文档

你可以通过 **http://localhost:8080/doc.html** 访问 OpenAPI 文档。该文档基于 __Swagger 2__ 并由 __Knife4J__ 增强。

故障排查
----------------------

### 启动 Aristotle 报 "Failed to execute CommandLineRunner" 错误

对于 Neo4J 数据库，Aristotle 会在首次连接数据库时自动
[创建若干数据库约束](https://github.com/paion-data/aristotle/blob/master/src/main/java/com/paiondata/aristotle/config/ConstraintInitializer.java)。如果 [启动](#starting-aristotle) 时出现如下报错：

```text
java.lang.IllegalStateException: Failed to execute CommandLineRunner
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:774) ~[spring-boot-2.7.3.jar!/:2.7.3]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:755) ~[spring-boot-2.7.3.jar!/:2.7.3]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) ~[spring-boot-2.7.3.jar!/:2.7.3]
	...
Caused by: org.neo4j.driver.exceptions.DatabaseException: Unable to create Constraint( name='constraint_1c8dc611', type='UNIQUENESS', schema=(:User {oidcid}) ):
Both Node(516397) and Node(517024) have the label `User` and property `oidcid` = 'user42fd5D645'. Note that only the first found violation is shown.
	at org.neo4j.driver.internal.util.Futures.blockingGet(Futures.java:111) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	at org.neo4j.driver.internal.InternalTransaction.run(InternalTransaction.java:58) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	at org.neo4j.driver.internal.AbstractQueryRunner.run(AbstractQueryRunner.java:34) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	...
```

很可能是 Neo4J 数据库中已存在部分数据，导致约束无法创建。建议清空数据库后重新启动 Aristotle。

[Aristotle]: https://aristotle-ws.com
