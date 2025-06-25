---
sidebar_position: 2
title: Service Configuration
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

This section describes the main configurations for the [Aristotle] service. All settings are located in the `src/main/resources/application.yaml` file.

### Configuration Loading Order

The system supports multiple configuration sources, which are loaded in the following order of precedence (higher-priority sources override lower-priority ones):

1.  **Operating System Environment Variables**: e.g., `export SERVER_PORT=8081`.
2.  **Java System Properties**: e.g., `java -jar app.jar -Dserver.port=8082`.
3.  **`application.yaml` File**: The configuration file within the project.

### Core Configurations

The following are the key configuration items in the `application.yaml` file.

#### 1. Server Configuration

```yaml
server:
  port: 8080 # Application service port
```

#### 2. Neo4j Database Configuration

```yaml
spring:
  data:
    neo4j:
      uri: bolt://localhost:7687
      database: neo4j
      username: neo4j
      password: your_password
```

-   **`uri`**: The connection URI for your Neo4j database.
-   **`database`**: The name of the database instance to connect to.
    -   Since Neo4j 4.x, multiple databases are supported. The Community Edition supports only the default `neo4j` database, while the Enterprise Edition allows for multiple databases. If you are using the Enterprise Edition with a custom database, change this value to your database name.
-   **`username`**: The database username.
-   **`password`**: The database password.

#### 3. Cache Configuration

The system supports two levels of caching, Caffeine (local) and Redis (distributed), which can be enabled or disabled independently.

```yaml
spring:
  # Caffeine local cache configuration
  read-cache:
    enabled: true          # true: enable, false: disable
    num-subgraphs: 500     # Maximum number of graphs to cache

  # Redis distributed cache configuration
  redis:
    enabled: false         # true: enable, false: disable
    host: 127.0.0.1
    port: 6379
    password: your_redis_password
```

:::tip
For a detailed explanation of the caching mechanism, please refer to the [Caching](./caching.md) documentation.
:::

[Aristotle]: https://github.com/paion-data/aristotle/
