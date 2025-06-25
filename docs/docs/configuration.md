---
sidebar_position: 2
title: 配置
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

本页所述的配置可以通过以下几种方式设置，优先级顺序如下：

1. [操作系统环境变量]；例如，可以通过如下命令设置环境变量：
   `export NEO4J_URI="bolt://db:7687"`
2. [Java 系统属性]；例如，可以通过如下方式设置 Java 系统属性：
   `System.setProperty("NEO4J_URI", "bolt://db:7687")`
3. 放置在 CLASSPATH 下的 **.yaml** 文件。该文件通常放在 `src/main/resources` 目录下，例如内容为 `uri: bolt://db:7687`

核心属性
---------------

:::note

以下配置建议放在名为 **application.yaml** 的配置文件中。

:::

- **username**：持久化数据库用户名（需有读写权限）。
- **password**：持久化数据库用户密码。
- **uri**：持久化数据库 URL，例如 "bolt://db:7687"。
- **database**：持久化数据库名称：

Neo4j 有两个自管理版本可选：社区版（CE）和企业版（EE）。企业版包含社区版的全部功能，并额外支持备份、集群和高可用等企业特性。

Neo4j 升级到 4.x 及以上版本并启动后，默认有两个库，如下图所示。目录也变为 data/databases/，登录后 neo4j 数据库为默认库。[官方介绍](https://neo4j.com/docs/operations-manual/4.0/introduction/)：

- **system**：系统数据库，包含数据库管理系统和安全配置的元数据；
- **neo4j**：默认数据库，存储用户数据，默认名称为 neo4j。

如果你使用 EE 版本并创建了多个数据库，请将 `database` 配置为你创建的数据库名称。

[Java 系统属性]: https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html

[操作系统环境变量]: https://docs.oracle.com/javase/tutorial/essential/environment/env.html
