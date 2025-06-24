---
sidebar_position: 3
title: 部署指南
---

# 部署指南

本节介绍如何将 Aristotle 服务部署到生产或测试环境。

## 1. 前置条件

- 已完成 [快速开始](/docs/setup) 步骤
- Neo4j 数据库已可用

## 2. 部署方式

### 方式一：Maven

适合开发和测试环境。

```bash
./mvnw spring-boot:run
```

### 方式二：Docker Compose

推荐用于生产或一键部署。

```bash
docker compose up -d
```

### 方式三：手动构建镜像

```bash
./mvnw clean package -DskipTests
# 构建镜像
# docker build -t aristotle-webservice:latest .
```

## 3. 配置环境变量

可通过环境变量覆盖 `application.yaml` 配置，例如：

```bash
export NEO4J_URI=bolt://your-neo4j-host:7687
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=yourpassword
```

## 4. 端口与健康检查

- 默认服务端口：8080
- 健康检查接口：`/actuator/health`

## 5. 日志与监控

- 日志输出到控制台，可配置文件输出
- 支持 Spring Boot Actuator 监控

## 6. 常见问题

- 容器无法连接数据库？请检查网络和环境变量
- 服务未启动？请检查日志输出

## 7. 参考

- [GitHub 仓库](https://github.com/Doom9527/aristotle-webservice)
