---
sidebar_position: 1
title: 快速开始
---

# 快速开始

欢迎使用 Aristotle 知识图谱服务！本指南将帮助你在几分钟内完成本地环境的搭建与服务启动。

## 1. 环境准备

- **JDK 17+**
- **Maven 3.6+**
- **Neo4j 4.x/5.x**（本地或远程均可）
- （可选）**Docker**

## 2. 获取代码

```bash
git clone https://github.com/Doom9527/aristotle-webservice.git
cd aristotle-webservice
```

## 3. 配置数据库

编辑 `src/main/resources/application.yaml`，根据你的 Neo4j 实例修改如下配置：

```yaml
spring:
  data:
    neo4j:
      uri: bolt://localhost:7687
      database: neo4j
      username: neo4j
      password: 12345678
```

## 4. 启动服务

### 方式一：Maven

```bash
./mvnw spring-boot:run
```

### 方式二：Docker

```bash
docker compose up
```

## 5. 访问 API 文档

服务启动后，访问 [http://localhost:8080/doc.html](http://localhost:8080/doc.html) 查看接口文档。

## 6. 常见问题

- 端口被占用？请检查 8080 端口是否被其他服务占用。
- Neo4j 连接失败？请确认数据库配置和网络连通性。

## 7. 参考

- [GitHub 仓库](https://github.com/Doom9527/aristotle-webservice)
- [项目主页](https://doom9527.github.io/blog/)
