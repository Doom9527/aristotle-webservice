---
sidebar_position: 4
title: 过滤
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

# Aristotle WS 的过滤功能

Aristotle WS 提供了强大的过滤能力，允许用户根据特定条件查询知识图谱中的节点。该功能通过实现有针对性的检索，提升了数据获取的效率和结果的相关性。

## 过滤功能的实现方式

过滤功能通过一个名为 `FilterQueryGraphDTO` 的数据传输对象（DTO）实现。它包含两个主要部分：

1. **uuid**：图谱的唯一标识符，指明要查询的具体图谱。
2. **properties**：一个 `Map<String, String>`，表示过滤条件。每个键值对的 key 对应图谱节点的属性，value 表示该属性的过滤值。

例如，用户可以按如下格式指定过滤条件：

```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
    "language": "En",
    "status": "false"
  }
}
```

上述请求会筛选出图谱中 `language` 为 "En" 且 `status` 为 "false" 的节点。

## 过滤逻辑实现细节

在服务层，业务逻辑通过 `getRelationByGraphUuid` 方法处理过滤。该方法会根据传入的 `uuid` 和 `properties` 动态构建 Cypher 查询语句：

```java
final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
        + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N1, properties.entrySet()) : "")
        + " OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N2, properties.entrySet()) : "")
        + " RETURN DISTINCT n1, r, n2";
```

其中，`getFilterProperties` 方法会将过滤条件转换为合适的 Cypher 查询片段。例如，若 `properties` 包含 `{ "language": "En", "Status": "false" }`，生成的 Cypher 查询类似：

```cypher
MATCH (g1:Graph { uuid: $uuid }) 
OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode { language: 'En', Status: 'false' }) 
OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode { language: 'En', Status: 'false' }) 
RETURN DISTINCT n1, r, n2
```

如果 `properties` 为空或未提供，则查询不会做额外过滤，返回所有符合条件的节点。

## 如何传递过滤条件

向 Aristotle WS 发送过滤请求时，用户需使用 POST 方法，并在请求体中包含图谱的 `uuid` 和 `properties`。例如：

POST /graph
Content-Type: application/json
```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
    "language": "En",
    "Status": "false"
  }
}
```

### 参数说明

1. **uuid**：必填，指定要查询的图谱唯一标识。
2. **properties**：可选，JSON 对象，表示过滤条件的键值对，需与节点属性匹配。可指定多个属性实现组合过滤。

### 响应示例

若过滤成功，服务会返回符合条件的节点和关系。例如，可能的响应如下：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "uuid": "3e308cd7b15c46bea971b43e090b18d2",
    "title": "语言图谱",
    "description": "与语言相关的图谱",
    "createTime": "2024-10-19 16:07:26",
    "updateTime": "2024-10-19 16:07:26",
    "nodes": [
      {
        "uuid": "2ab78d7c532b41cda028084fd8a5cdd3",
        "properties": {
          "Status": "false",
          "language": "En",
          "exercitatione9": "reprehenderit tempor minim ad qui"
        },
        "createTime": "2024-10-19 16:07:26",
        "updateTime": "2024-10-19 16:07:26"
      }
    ],
    "relations": []
  }
}
```

## 过滤功能的应用场景

- **精准数据筛选**：过滤功能可在大型图谱中快速筛选特定节点，如筛选活跃/非活跃用户或特定语言内容。
- **多条件组合查询**：`properties` 对象支持多条件组合，用户可同时按多属性过滤节点，如语言和状态。
- **默认返回全部数据**：若未提供 `properties` 或传递空对象，系统将返回所有节点和关系数据。
