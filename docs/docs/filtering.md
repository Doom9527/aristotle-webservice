---
sidebar_position: 4
title: 过滤与查询
---

# 过滤与查询

本节介绍 Aristotle API 的过滤与查询能力。

## 1. 节点与关系过滤

支持通过属性过滤节点和关系。例如：

```json
{
  "property": "value"
}
```

## 2. 分页与排序

- 支持分页参数：`pageNumber`、`pageSize`
- 支持排序参数：`orderBy`

## 3. 复杂查询

可通过组合多个过滤条件实现复杂查询。

## 4. 示例

```json
{
  "uuid": "graph-uuid",
  "properties": {
    "name": "张三"
  },
  "pageNumber": 1,
  "pageSize": 10
}
```

## 5. 参考

- [API 文档](http://localhost:8080/doc.html)
