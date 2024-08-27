package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH (u:User) WHERE elementId(u) = $elementId RETURN u")
    User getUserByElementId(String elementId);

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN u")
    User getUserByUidcid(String uidcid);

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN count(u)")
    long checkUidcidExists(String uidcid);

    @Query("MATCH (u:User) WHERE elementId(u) = $elementId RETURN count(u)")
    long checkElementIdExists(String elementId);

    @Query("CREATE (u:User { uidcid: $uidcid, nick_name: $nickName }) RETURN u")
    User createUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    @Query("MATCH (u:User) WHERE elementId(u) = $elementId SET u.nick_name = $nickName RETURN u")
    User updateUser(@Param("elementId") String elementId,
                    @Param("nickName") String nickName);

    @Query("MATCH (u:User) WHERE elementId(u) IN $elementIds RETURN count(u)")
    long countByElementIds(List<String> elementIds);

    @Query("MATCH (u:User) WHERE elementId(u) IN $elementIds DETACH DELETE u")
    void deleteByElementIds(List<String> elementIds);

    @Query("MATCH (u:User) WHERE elementId(u) IN $elementIds "
            + "WITH u "
            + "MATCH (u)-[r:RELATION]->(g:Graph) "
            + "RETURN elementId(g)")
    List<String> getGraphElementIdsByUserId(List<String> elementIds);

    @Query("MATCH (u:User) WHERE elementId(u) = $elementId "
            + "WITH u "
            + "MATCH (u)-[r:RELATION]->(g:Graph) "
            + "RETURN g")
    List<Graph> getGraphByUserId(String elementId);
}