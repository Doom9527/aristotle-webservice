---
sidebar_position: 5
title: Caching
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

Aristotle is designed to serve knowledge graphs with billions of nodes and relationships. Facing such a large-scale dataset, how to efficiently support CRUD operations, especially the common _expand_ operation, is a core challenge in system design. Under massive data, expand operations often lead to:

1. Excessive query latency
2. Query timeouts (e.g., memory overflow)

This section combines practical engineering experience to introduce how Aristotle solves the above problems through multi-level caching and distributed cache loading mechanisms.

Handling Long-Running Queries
-----------------------------

In high-concurrency and large-data scenarios, even small-scale subgraph expansions can put great pressure on the database, resulting in slow queries. Therefore, Aristotle adopts a multi-level caching mechanism combining local and distributed caches, balancing query performance and system scalability.

### Multi-Level Cache Architecture

The system uses Caffeine as the local cache and Redis as the distributed cache, forming the following layered structure:

```
┌───────────────┐
│ Application   │
├───────────────┤
│   Caffeine    │ ← Local cache (L1, ultra-low latency)
│   (JVM RAM)   │
├───────────────┤
│   Redis       │ ← Distributed cache (L2, multi-node sharing)
│ (Remote store)│
├───────────────┤
│   Neo4j       │ ← Data source
│ (Database)    │
└───────────────┘
```

- **Caffeine**: Stores frequently accessed hot data, providing the fastest access speed.
- **Redis**: Stores precomputed subgraph data, supports distributed access and high capacity.
- **Configurable Switches**: Each cache type can be enabled/disabled independently, flexibly adapting to different business needs.

### Caching Strategy and Implementation

Uses the classic Cache-Aside Pattern:

- On query, check Caffeine first. If not hit, check Redis. If still not hit, query the database and write back to the cache.
- On data update or deletion, actively clear both local and distributed caches to ensure consistency.

#### Configuration Example

```yaml
spring:
  read-cache:
    enabled: true          # Enable Caffeine cache
    num-subgraphs: 500     # Max cache entries
  redis:
    enabled: true          # Enable Redis cache
    host: 127.0.0.1
    port: 6379
```

:::info[Configurable Mechanism]
- You can flexibly control cache enable/disable via configuration files.
- Supports custom parameters such as max cache entries and expiration time.
:::

#### Code Snippet

Take Spring Boot as an example, Caffeine and Redis switches:

```java
@Configuration
public class CaffeineConfig {
    @Value("${spring.read-cache.enabled:true}")
    private boolean cacheEnabled;
    @Bean
    public Cache<String, GraphVO> graphCache() {
        if (!cacheEnabled) {
            return Caffeine.newBuilder().build(); // Empty cache
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
    // Only effective when Redis is enabled
}
```

### Advantages of Multi-Level Caching

- **Extreme Performance**: Local cache hits first, greatly reducing remote access and database pressure.
- **High Availability**: Redis supports distributed deployment, data can be shared across multiple nodes.
- **Flexible Scalability**: Cache strategies and capacity can be dynamically adjusted according to business needs.

Handling Timeout Queries
-----------------

For timeout issues caused by large data volumes, the system adopts a "precomputation + distributed cache loader" solution. Through an independent cache loading service, batch compute and cache subgraph expansion results, greatly improving subsequent query efficiency.

### Distributed Cache Loader Design

Taking Neo4j as an example, the process is as follows:

1. **Batch Traverse Nodes**: Scheduled tasks batch traverse all nodes, use BFS to compute max expansion depth.
2. **Store Results in Redis**: Store each node's expansion subgraph and depth info in Redis as cache for subsequent queries.
3. **Checkpoint & Progress Management**: Supports checkpoint resume to avoid redundant computation and improve task robustness.

#### Key Code Snippet

```java
@Slf4j
@Service
public class CacheLoaderService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private Neo4jService neo4jService;
    public synchronized void startLoading() {
        // Checkpoint resume
        Integer start = (Integer) redisTemplate.opsForValue().get("task:progress");
        if (start == null) start = 0;
        // Batch loading and BFS expansion
        // ...
        // Store to Redis
        redisTemplate.opsForValue().set("graph:" + node.getUuid() + ":data", graph);
        redisTemplate.opsForValue().set("graph:" + node.getUuid() + ":maxDepth", graph.getMaxDepth());
    }
}
```

:::tip
The distributed cache loader can be deployed independently, supports scheduled batch preheating, and is suitable for efficient expansion queries on large-scale knowledge graphs.
:::

### Redis Data Structure Example

- `graph:{nodeUuid}:data`: Stores the expansion subgraph data of the node
- `graph:{nodeUuid}:maxDepth`: Stores the max expansion depth of the node
- `task:progress`: Records the progress of cache loading tasks, supports checkpoint resume

---

Through the above multi-level cache and distributed cache loading mechanism, Aristotle can efficiently handle high-concurrency expansion queries and timeout issues on large-scale knowledge graphs, greatly improving system stability and response speed.

For more details on cache configuration and advanced usage, please refer to the relevant source code and configuration documentation.

---

To insert architecture diagrams or cache flowcharts, refer to the image import method in the original document:

```md
import RedisCache from './img/redis-caching.png';

<div align="center"> 
    <img src={RedisCache} width="80%"/>
</div>
```

If you need more detailed code implementation or configuration details, please let us know!
