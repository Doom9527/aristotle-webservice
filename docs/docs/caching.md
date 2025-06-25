---
sidebar_position: 5
title: 缓存机制
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

Aristotle 旨在服务于数亿级节点和关系的知识图谱。面对如此大规模的数据，如何高效支持 CRUD 操作，尤其是常见的 _expand_（扩展）操作，是系统设计的核心挑战。大数据量下，扩展操作常常导致：

1. 查询耗时过长
2. 查询超时（如内存溢出）

本节将结合实际工程经验，介绍 Aristotle 如何通过多级缓存与分布式缓存加载机制，解决上述问题。

处理长时间查询
-----------------------------

在高并发和大数据量场景下，即使是小规模的子图扩展，也可能对数据库造成极大压力，导致查询变慢。为此，Aristotle 采用了本地+分布式的多级缓存机制，兼顾查询性能与系统可扩展性。

### 多级缓存架构

系统采用 Caffeine 作为本地缓存，Redis 作为分布式缓存，形成如下层次结构：

```
┌───────────────┐
│   应用层      │
├───────────────┤
│   Caffeine    │ ← 本地缓存（一级缓存，极低延迟）
│   (JVM内存)   │
├───────────────┤
│   Redis       │ ← 分布式缓存（二级缓存，支持多节点共享）
│   (远程存储)  │
├───────────────┤
│   Neo4j       │ ← 数据源
│   (数据库)    │
└───────────────┘
```

- **Caffeine**：存储频繁访问的热点数据，提供最快的访问速度。
- **Redis**：存储预计算的子图数据，支持分布式访问和高容量。
- **可配置开关**：支持独立开启/关闭每种缓存类型，灵活适配不同业务需求。

### 缓存策略与实现

采用经典的旁路缓存（Cache-Aside Pattern）：

- 查询时，优先查 Caffeine，本地未命中则查 Redis，仍未命中再查数据库，并回写缓存。
- 数据更新或删除时，主动清理本地和分布式缓存，保证一致性。

#### 配置示例

```yaml
spring:
  read-cache:
    enabled: true          # Caffeine缓存开关
    num-subgraphs: 500     # 缓存条目数量限制
  redis:
    enabled: true          # Redis缓存开关
    host: 127.0.0.1
    port: 6379
```

:::info[可配置机制]
- 可通过配置文件灵活控制缓存的启用与禁用。
- 支持最大缓存条数、过期时间等参数自定义。
:::

#### 代码片段

以 Spring Boot 为例，Caffeine 和 Redis 的开关控制：

```java
@Configuration
public class CaffeineConfig {
    @Value("${spring.read-cache.enabled:true}")
    private boolean cacheEnabled;
    @Bean
    public Cache<String, GraphVO> graphCache() {
        if (!cacheEnabled) {
            return Caffeine.newBuilder().build(); // 空缓存
        }
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();
    }
}
```

```java
@Configuration
@ConditionalOnProperty(value = "spring.redis.enabled", havingValue = "true")
public class RedisConfig {
    // 仅在Redis启用时生效
}
```

### 多级缓存的优势

- **极致性能**：本地缓存优先命中，极大减少远程访问和数据库压力。
- **高可用性**：Redis 支持分布式部署，数据可在多节点间共享。
- **灵活扩展**：可根据业务需求动态调整缓存策略和容量。

处理超时查询
-----------------

对于因数据量过大导致的超时问题，系统采用"预计算+分布式缓存加载器"方案。通过独立的缓存加载服务，批量计算并缓存子图扩展结果，极大提升后续查询效率。

### 分布式缓存加载器设计

以 Neo4j 为例，采用如下流程：

1. **批量遍历节点**：定时任务批量遍历所有节点，使用 BFS 算法计算最大深度扩展。
2. **结果存入 Redis**：将每个节点的扩展图及深度信息存入 Redis，作为后续查询的缓存。
3. **断点续传与进度管理**：支持断点续传，避免重复计算，提升任务健壮性。

#### 关键代码片段

```java
@Slf4j
@Service
public class CacheLoaderService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private Neo4jService neo4jService;
    public synchronized void startLoading() {
        // 断点续传
        Integer start = (Integer) redisTemplate.opsForValue().get("task:progress");
        if (start == null) start = 0;
        // 批量加载与BFS扩展
        // ...
        // 存储到Redis
        redisTemplate.opsForValue().set("graph:" + node.getUuid() + ":data", graph);
        redisTemplate.opsForValue().set("graph:" + node.getUuid() + ":maxDepth", graph.getMaxDepth());
    }
}
```

:::tip
分布式缓存加载器可独立部署，支持定时批量预热缓存，适合大规模知识图谱的高效扩展查询。
:::

### Redis 数据结构示例

- `graph:{nodeUuid}:data`：存储节点的扩展图数据
- `graph:{nodeUuid}:maxDepth`：存储节点的最大扩展深度
- `task:progress`：记录缓存加载任务进度，支持断点续传

---

通过上述多级缓存与分布式缓存加载机制，Aristotle 能够高效应对大规模知识图谱下的高并发扩展查询与超时问题，极大提升系统的稳定性与响应速度。

如需进一步了解缓存配置与高级用法，请参考相关源码与配置文档。
