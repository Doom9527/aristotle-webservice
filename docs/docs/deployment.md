---
sidebar_position: 3
title: 应用部署
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

本节介绍如何在服务器上部署 [Aristotle] 服务。

### 1. 环境准备

部署前，请确保服务器已安装以下软件：

- **Java 17**: 用于运行应用。
- **Maven**: 用于构建项目（如果需要在服务器上构建）。
- **Docker & Docker Compose**: 用于容器化部署（推荐）。

:::note
我们假设服务器环境为 `Ubuntu 22.04+`。关于 Java 和 Maven 的安装，请参考 [本地开发环境设置](./setup.md) 中的步骤。
:::

### 2. 构建与打包

首先，克隆代码仓库并执行 Maven 打包命令。你需要提供数据库相关的环境变量来完成打包。

```bash
# 1. 克隆仓库
git clone https://github.com/paion-data/aristotle.git
cd aristotle

# 2. 设置数据库环境变量 (用于运行测试，如果跳过测试则非必须)
export NEO4J_URI=YOUR_NEO4J_URI
export NEO4J_USERNAME=YOUR_NEO4J_USERNAME
export NEO4J_PASSWORD=YOUR_NEO4J_PASSWORD
export NEO4J_DATABASE=YOUR_NEO4J_DATABASE

# 3. 执行打包
# -Dmaven.test.skip=true 会跳过单元测试，加快打包速度
mvn clean package -Dmaven.test.skip=true
```

命令成功后，你会在 `target/` 目录下找到 `Aristotle-1.0-SNAPSHOT.jar` 文件。

### 3. 部署方式

我们提供两种推荐的部署方式。

#### 方式一：Java Jar 包直接运行

这是最基础的部署方式。

```bash
java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

**配置管理**:

你可以通过在命令前追加环境变量来覆盖 `application.yaml` 中的默认配置。

```bash
# 覆盖数据库配置和服务器端口
export SPRING_DATA_NEO4J_URI=bolt://prod-neo4j:7687
export SPRING_DATA_NEO4J_USERNAME=prod_user
export SPRING_DATA_NEO4J_PASSWORD=prod_password
export SERVER_PORT=8080

java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

:::tip[环境变量命名]
注意 Spring Boot 官方推荐的环境变量格式，例如 `spring.data.neo4j.uri` 对应的环境变量是 `SPRING_DATA_NEO4J_URI`。
:::

#### 方式二：使用 Docker Compose 部署 (推荐)

这是在生产环境中推荐的部署方式，能够方便地管理应用和其依赖（如 Neo4j 数据库）。

项目根目录下的 `docker-compose.yml` 和 `Dockerfile` 已经为您准备好了一切。

```bash
# 在项目根目录下执行
# -d 参数表示在后台运行
docker-compose up -d --build
```

`--build` 参数会强制重新构建镜像，确保使用的是最新的代码。

**配置管理**:

直接修改 `docker-compose.yml` 文件中的 `environment` 部分，即可完成服务配置。

### 4. 服务访问

服务启动后：

- **应用端口**: 默认为 `8080`。
- **API 文档**: 访问 `http://<服务器IP>:8080/doc.html` 查看 OpenAPI (Swagger) 文档。
- **健康检查**: 访问 `http://<服务器IP>:8080/actuator/health` 查看应用健康状态。

### 5. 故障排查

**问题：启动时报 "Failed to execute CommandLineRunner" 或 "constraint" 相关错误**

```text
Caused by: org.neo4j.driver.exceptions.DatabaseException: Unable to create Constraint ...
```

**原因**: 服务在首次启动时会自动在 Neo4j 数据库中创建唯一性约束。如果你的数据库中已经存在不符合约束的数据（例如，两个用户节点有相同的 `oidcid`），则会创建失败并报错。

**解决方案**: 建议清空数据库中的数据，然后重启服务。

[Aristotle]: https://aristotle-ws.com
