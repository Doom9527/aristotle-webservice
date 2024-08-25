package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    //TODO 需要改回elementId
    @Query("MATCH (u:User) WHERE id(u) = $id RETURN u")
    User getUserById(Long id);

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN u")
    User getUserByUidcid(String uidcid);

    @Query("MATCH (u:User { uidcid: $uidcid }) RETURN count(u)")
    long checkUidcidExists(String uidcid);

    //TODO 需要改回elementId
    @Query("MATCH (u:User) WHERE id(u) = $id RETURN count(u)")
    long checkIdExists(Long id);

    @Query("CREATE (u:User { uidcid: $uidcid, nick_name: $nickName }) RETURN u")
    User createUser(@Param("uidcid") String uidcid,
                    @Param("nickName") String nickName);

    @Query("MATCH (u:User { id: $id }) SET u.nick_name = $nickName RETURN u")
    User updateUser(@Param("id") Long id,
                    @Param("nickName") String nickName);
}