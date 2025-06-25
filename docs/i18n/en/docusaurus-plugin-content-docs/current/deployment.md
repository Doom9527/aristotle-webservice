---
sidebar_position: 3
title: Application Deployment
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

This section describes how to deploy the [Aristotle] service on a server.

### 1. Prerequisites

Before deploying, ensure the following software is installed on your server:

- **Java 17**: To run the application.
- **Maven**: To build the project (if you build on the server).
- **Docker & Docker Compose**: For containerized deployment (recommended).

:::note
This guide assumes a server environment like `Ubuntu 22.04+`. For Java and Maven installation, please refer to the [Local Development Setup](./setup.md) guide.
:::

### 2. Build and Package

First, clone the repository and run the Maven package command. You may need to provide database environment variables to run the tests, or you can skip them.

```bash
# 1. Clone the repository
git clone git@github.com:Doom9527/aristotle-webservice.git
cd aristotle-webservice

# 2. Set database environment variables (optional, for running tests)
export NEO4J_URI=YOUR_NEO4J_URI
export NEO4J_USERNAME=YOUR_NEO4J_USERNAME
export NEO4J_PASSWORD=YOUR_NEO4J_PASSWORD
export NEO4J_DATABASE=YOUR_NEO4J_DATABASE

# 3. Package the application
# -Dmaven.test.skip=true will skip tests and speed up the build
mvn clean package -Dmaven.test.skip=true
```

After the command succeeds, you will find the `Aristotle-1.0-SNAPSHOT.jar` file in the `target/` directory.

### 3. Deployment Methods

We provide two recommended methods for deployment.

#### Method 1: Run with `java -jar`

This is the most basic way to run the service.

```bash
java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

**Configuration Management**:

You can override the default settings in `application.yaml` by setting environment variables before running the `java` command.

```bash
# Override database configuration and server port
export SPRING_DATA_NEO4J_URI=bolt://prod-neo4j:7687
export SPRING_DATA_NEO4J_USERNAME=prod_user
export SPRING_DATA_NEO4J_PASSWORD=prod_password
export SERVER_PORT=8080

java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

:::tip[Environment Variable Naming]
Note that Spring Boot recommends a specific format for environment variables. For example, `spring.data.neo4j.uri` becomes `SPRING_DATA_NEO4J_URI`.
:::

#### Method 2: Deploy with Docker Compose (Recommended)

This is the recommended method for production environments, as it allows you to easily manage the application and its dependencies (like a Neo4j database) together.

The `docker-compose.yml` and `Dockerfile` in the project root are already set up for you.

```bash
# From the project root directory
# The -d flag runs the services in the background
docker-compose up -d --build
```

The `--build` flag forces a rebuild of the Docker image, ensuring you are using the latest code.

**Configuration Management**:

You can configure the service by modifying the `environment` section in the `docker-compose.yml` file.

### 4. Service Access

Once the service is running:

- **Application Port**: Defaults to `8080`.
- **API Documentation**: Access `http://<your-server-ip>:8080/doc.html` to view the OpenAPI (Swagger) documentation.
- **Health Check**: Access `http://<your-server-ip>:8080/actuator/health` to check the application's health status.

### 5. Troubleshooting

**Problem: Startup fails with "Failed to execute CommandLineRunner" or a "constraint" related error.**

```text
Caused by: org.neo4j.driver.exceptions.DatabaseException: Unable to create Constraint ...
```

**Reason**: On its first run, the service automatically creates unique constraints in the Neo4j database. If your database already contains data that violates these constraints (e.g., two user nodes with the same `oidcid`), the constraint creation will fail, and the application will stop.

**Solution**: It is recommended to clear the conflicting data from your database and restart the service.

[Aristotle]: https://aristotle-ws.com
