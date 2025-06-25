---
sidebar_position: 2
title: 服务配置
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

本节介绍 [Aristotle] 服务的主要配置项。所有配置均位于 `src/main/resources/application.yaml` 文件中。

### 配置加载顺序

系统支持多种配置方式，加载优先级从高到低如下：

1.  **操作系统环境变量**: 例如 `export SERVER_PORT=8081`。
2.  **Java 系统属性**: 例如 `java -jar app.jar -Dserver.port=8082`。
3.  **`application.yaml` 文件**: 项目根目录下的配置文件。

高优先级的配置会覆盖低优先级的同名配置。

### 核心配置

以下是 `application.yaml` 文件中的关键配置项说明。

#### 1. 服务器配置

```yaml
server:
  port: 8080 # 应用服务端口
```

#### 2. Neo4j 数据库配置

```yaml
spring:
  data:
    neo4j:
      uri: bolt://localhost:7687
      database: neo4j
      username: neo4j
      password: your_password
```

-   **`uri`**: Neo4j 数据库的连接地址。
-   **`database`**: 要连接的数据库实例名。
    -   Neo4j 4.x 版本后支持多数据库。社区版（Community Edition）只支持默认的 `neo4j` 库；企业版（Enterprise Edition）支持创建多个数据库。如果你使用的是企业版并创建了自定义数据库，请将此值修改为你的数据库名。
-   **`username`**: 数据库用户名。
-   **`password`**: 数据库密码。

#### 3. 缓存配置

系统支持 Caffeine（本地缓存）和 Redis（分布式缓存）两级缓存，可独立开关。

```yaml
spring:
  # Caffeine 本地缓存配置
  read-cache:
    enabled: true          # true: 开启, false: 关闭
    num-subgraphs: 500     # 缓存图谱的最大数量

  # Redis 分布式缓存配置
  redis:
    enabled: false         # true: 开启, false: 关闭
    host: 127.0.0.1
    port: 6379
    password: your_redis_password
```

:::tip
关于缓存机制的详细介绍，请参考 [Caching](./caching.md) 文档。
:::

[Aristotle]: https://github.com/paion-data/aristotle/
