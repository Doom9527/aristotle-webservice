---
sidebar_position: 4
title: 按属性过滤
---

# 按属性过滤 (Filtering)

[Aristotle] 提供了强大的节点属性过滤功能，允许用户在查询图谱时，根据一个或多个属性筛选出符合特定条件的节点，从而实现更精确、高效的数据检索。

### 实现原理

该功能主要通过向图谱查询接口 `POST /graph` 发送一个包含过滤条件的数据传输对象（DTO）`FilterQueryGraphDTO` 来实现。

#### 请求体格式

`FilterQueryGraphDTO` 包含两个核心字段：

1.  `uuid` (string, **必填**): 要查询的图谱的唯一标识符。
2.  `properties` (object, **可选**): 一个键值对集合，用于定义过滤条件。
    -   `key`: 节点属性名 (Node Property Key)。
    -   `value`: 要匹配的属性值 (Property Value)。

#### 请求示例

例如，要查询指定图谱中所有 `language` 属性为 `"En"` 且 `status` 属性为 `"false"` 的节点，请求体如下：

```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
    "language": "En",
    "status": "false"
  }
}
```

### 后端实现逻辑

服务端的 `getRelationByGraphUuid` 方法接收到请求后，会动态地构建 [Cypher](https://neo4j.com/developer/cypher/) 查询语句。

核心逻辑如下：

```java
// 基础查询语句，用于匹配图谱 (g1) 及其下的所有节点 (n1) 和关系 (r)
final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
        + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
        // 如果 properties 不为空，则为节点 n1 添加过滤条件
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N1, properties.entrySet()) : "")
        + " OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
        // 如果 properties 不为空，则为节点 n2 添加过滤条件
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N2, properties.entrySet()) : "")
        + " RETURN DISTINCT n1, r, n2";
```

其中，`getFilterProperties` 方法负责将 `properties` 这个 Map 转换为 Cypher 的 `WHERE` 子句片段。它会遍历 Map 中的每个条目，并将其拼接成 `{ key1: 'value1', key2: 'value2' }` 的形式。

因此，对于上面的请求示例，最终生成的 Cypher 查询语句大致如下：

```cypher
MATCH (g1:Graph { uuid: "3e308cd7b15c46bea971b43e090b18d2" })
OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode { language: 'En', status: 'false' })
OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode { language: 'En', status: 'false' })
RETURN DISTINCT n1, r, n2
```

如果请求中不包含 `properties` 字段或该字段为空对象，那么查询将不会附加任何过滤条件，直接返回该图谱下的所有节点和关系。

:::info[当前支持的查询方式]
目前，`properties` 过滤只支持 **精确匹配** (Exact Match)。暂不支持模糊查询、范围查询等复杂匹配方式。
:::

### 响应示例

若请求成功，服务会返回经过滤后的节点和关系组成的子图。

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "uuid": "3e308cd7b15c46bea971b43e090b18d2",
    "title": "语言图谱",
    "description": "与语言相关的图谱",
    "nodes": [
      {
        "uuid": "2ab78d7c532b41cda028084fd8a5cdd3",
        "properties": {
          "status": "false",
          "language": "En",
          "exercitatione9": "reprehenderit tempor minim ad qui"
        },
        "createTime": "2024-10-19 16:07:26",
        "updateTime": "2024-10-19 16:07:26"
      }
      // ... 其他符合条件的节点
    ],
    "relations": [
      // ... 符合条件的关系
    ]
  }
}
```

[Aristotle]: https://github.com/paion-data/aristotle/
