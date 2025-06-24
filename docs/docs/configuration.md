---
sidebar_position: 2
title: 配置说明
---

# 配置说明

本节介绍 Aristotle 服务的主要配置项及其作用。

## 1. Neo4j 数据库配置

在 `application.yaml` 中配置：

```yaml
spring:
  data:
    neo4j:
      uri: bolt://localhost:7687
      database: neo4j
      username: neo4j
      password: 12345678
```

## 2. 缓存配置

支持 Caffeine 本地缓存和 Redis 分布式缓存，可通过如下配置开关：

```yaml
spring:
  read-cache:
    enabled: true          # 启用Caffeine缓存
    num-subgraphs: 500     # 缓存条目数量限制
  redis:
    enabled: false         # 启用Redis缓存
    host: 127.0.0.1
    port: 6379
    password: 123321
```

## 3. 端口与服务

```yaml
server:
  port: 8080
```

## 4. 其他常用配置

- 日志级别、Swagger、Knife4j 等可在 `application.yaml` 中自定义。

## 5. 参考

- [完整配置示例](https://github.com/Doom9527/aristotle-webservice/blob/master/src/main/resources/application.yaml)

本页所述的配置可以通过以下几种方式设置，优先级如下：

1. 操作系统环境变量，例如：
   `export NEO4J_URI="bolt://db:7687"`
2. Java 系统属性，例如：
   `System.setProperty("NEO4J_URI", "bolt://db:7687")`
3. 放置在 CLASSPATH 下的 **.yaml** 文件。通常放在 `src/main/resources` 目录下，例如：
   `uri: bolt://db:7687`

核心属性说明
------------

:::note
以下配置项建议放在名为 **application.yaml** 的配置文件中。
:::

- **username**：持久化数据库用户名（需有读写权限）
- **password**：持久化数据库用户密码
- **uri**：持久化数据库 URL，例如 "bolt://db:7687"
- **database**：持久化数据库名称

Neo4j 有两个版本可选：社区版（CE）和企业版（EE）。企业版包含社区版全部功能，并额外支持备份、集群和高可用等企业特性。

Neo4j 4.x 及以上版本启动后，默认有两个库，目录为 data/databases/，neo4j 为默认库。详见[官方介绍](https://neo4j.com/docs/operations-manual/4.0/introduction/)：

- **system**：系统数据库，包含数据库管理和安全配置元数据
- **neo4j**：默认数据库，存储用户数据，默认名称为 neo4j

如果你使用 EE 版本并创建了多个数据库，请将 `database` 配置为你创建的数据库名称。

[Java 系统属性]: https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html

[操作系统环境变量]: https://docs.oracle.com/javase/tutorial/essential/environment/env.html

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
