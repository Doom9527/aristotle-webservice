package com.paiondata.aristotle.service;

import java.util.List;
import java.util.Map;

public interface Neo4jService {

    List<Map<String, Object>> getGraphByUserUidcid(String uidcid);
}
