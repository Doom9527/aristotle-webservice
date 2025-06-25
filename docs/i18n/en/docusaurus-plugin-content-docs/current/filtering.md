---
sidebar_position: 4
title: Filtering by Properties
---

# Filtering by Properties

[Aristotle] provides powerful node filtering capabilities, allowing users to query for nodes that match specific attribute criteria, resulting in more precise and efficient data retrieval.

### How It Works

The feature is implemented by sending a `POST` request to the `/graph` endpoint with a `FilterQueryGraphDTO` object in the request body.

#### Request Body Format

The `FilterQueryGraphDTO` has two main fields:

1.  `uuid` (string, **required**): The unique identifier of the graph you want to query.
2.  `properties` (object, **optional**): A key-value map defining the filtering conditions.
    -   `key`: The node property key.
    -   `value`: The property value to match.

#### Example Request

To query for nodes where the `language` property is `"En"` and the `status` property is `"false"`, the request body would be:

```json
{
  "uuid": "3e308cd7b15c46bea971b43e090b18d2",
  "properties": {
    "language": "En",
    "status": "false"
  }
}
```

### Backend Implementation Logic

On the backend, the `getRelationByGraphUuid` method dynamically constructs a [Cypher](https://neo4j.com/developer/cypher/) query based on the request.

Here is the core logic:

```java
// Base query to match the graph (g1) and its related nodes (n1, n2) and relationships (r)
final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
        + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
        // Append filtering conditions for node n1 if properties are provided
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N1, properties.entrySet()) : "")
        + " OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
        // Append filtering conditions for node n2 if properties are provided
        + (properties != null && !properties.isEmpty() ?
        getFilterProperties(Constants.NODE_ALIAS_N2, properties.entrySet()) : "")
        + " RETURN DISTINCT n1, r, n2";
```

The `getFilterProperties` method is responsible for converting the `properties` map into a Cypher `WHERE` clause fragment. It iterates over the map entries and builds a string in the format `{ key1: 'value1', key2: 'value2' }`.

For the example request above, the final generated Cypher query would be approximately:

```cypher
MATCH (g1:Graph { uuid: "3e308cd7b15c46bea971b43e090b18d2" })
OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode { language: 'En', status: 'false' })
OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode { language: 'En', status: 'false' })
RETURN DISTINCT n1, r, n2
```

If the `properties` field is omitted or is an empty object, no filtering conditions are appended, and the query returns all nodes and relationships in the graph.

:::info[Supported Query Types]
Currently, property filtering only supports **exact matches**. More complex queries like fuzzy matching or range searches are not yet implemented.
:::

### Example Response

A successful request will return a subgraph containing only the nodes and relationships that match the filter criteria.

```json
{
  "code": 200,
  "msg": "Successful operation",
  "data": {
    "uuid": "3e308cd7b15c46bea971b43e090b18d2",
    "title": "Language Graph",
    "description": "Language related graph",
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
      // ... other matching nodes
    ],
    "relations": [
      // ... matching relations
    ]
  }
}
```

[Aristotle]: https://github.com/paion-data/aristotle/
