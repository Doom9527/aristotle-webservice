/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.impl.GraphServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for the Graph Service.
 * Uses Mockito to mock dependencies and validate graph service operations.
 */
@ExtendWith(MockitoExtension.class)
public class GraphServiceSpec {

    @InjectMocks
    private GraphServiceImpl graphService;

    @Mock
    private UserService userService;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private Neo4jService neo4jService;

    /**
     * Setup method to initialize mocks and test data.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a GraphVO by UUID returns the correct GraphVO when the graph exists.
     */
    @Test
    void getGraphVOByUuidGraphExistReturnGraphVO() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final String title = TestConstants.TEST_TILE1;
        final String description = TestConstants.TEST_DESCRIPTION1;
        final String currentTime = getCurrentTime();
        final Graph graph = Graph.builder()
                .uuid(uuid)
                .title(title)
                .description(description)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        final List<Map<String, Map<String, Object>>> nodes =
                Collections.singletonList(Collections.singletonMap("node",
                        Collections.singletonMap("id", "test-node-id")));

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);
        when(neo4jService.getGraphNodeByGraphUuid(uuid)).thenReturn(nodes);

        // Act
        final GraphVO graphVO = graphService.getGraphVOByUuid(uuid);

        // Assert
        Assertions.assertEquals(uuid, graphVO.getUuid());
        Assertions.assertEquals(title, graphVO.getTitle());
        Assertions.assertEquals(description, graphVO.getDescription());
        Assertions.assertEquals(currentTime, graphVO.getCreateTime());
        Assertions.assertEquals(currentTime, graphVO.getUpdateTime());
        Assertions.assertEquals(nodes, graphVO.getNodes());

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(neo4jService, times(1)).getGraphNodeByGraphUuid(uuid);
    }

    /**
     * Tests that getting a GraphVO by UUID throws a GraphNullException when the graph does not exist.
     */
    @Test
    void getGraphVOByUuidGraphDoesNotExistThrowsGraphNullException() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.getGraphVOByUuid(uuid));

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(neo4jService, never()).getGraphNodeByGraphUuid(uuid);
    }

    /**
     * Tests that getting a Graph by UUID returns the correct Graph when it exists.
     */
    @Test
    void getGraphByUuidGraphExistsReturnGraph() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final String currentTime = getCurrentTime();
        final Graph graph = Graph.builder()
                .uuid(uuid)
                .title(TestConstants.TEST_TILE1)
                .description(TestConstants.TEST_DESCRIPTION1)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);

        // Act
        final Optional<Graph> graphOptional = graphService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertTrue(graphOptional.isPresent());
        Assertions.assertEquals(graph, graphOptional.get());
    }

    /**
     * Tests that getting a Graph by UUID returns an empty Optional when the graph does not exist.
     */
    @Test
    void getGraphByUuidGraphDoesNotExistReturnsEmptyOptional() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act
        final Optional<Graph> graphOptional = graphService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertFalse(graphOptional.isPresent());
    }

    /**
     * Tests that creating and binding a graph with valid input results in successful creation.
     */
    @Test
    void createAndBindGraphValidInputGraphCreatedSuccessfully() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String title = TestConstants.TEST_TILE1;
        final String description = TestConstants.TEST_DESCRIPTION1;
        final GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                .userUidcid(uidcid)
                .title(title)
                .description(description)
                .build();

        final User user = User.builder()
                .uidcid(uidcid)
                .build();
        when(userService.getUserByUidcid(uidcid)).thenReturn(Optional.of(user));

        // Act
        graphService.createAndBindGraph(graphCreateDTO);

        // Assert
        verify(graphRepository).createAndBindGraph(
                eq(title),
                eq(description),
                eq(uidcid),
                any(String.class), // graphUuid
                any(String.class), // relationUuid
                any(String.class)  // currentTime
        );
    }

    /**
     * Tests that creating and binding a graph throws a UserNullException when the user is not found.
     */
    @Test
    void createAndBindGraphUserNotFoundThrowsUserNullException() {
        // Arrange
        final GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                .title(TestConstants.TEST_TILE1)
                .description(TestConstants.TEST_DESCRIPTION1)
                .userUidcid(TestConstants.TEST_ID1)
                .build();

        // Act & Assert
        assertThrows(UserNullException.class, () -> graphService.createAndBindGraph(graphCreateDTO));
    }

    /**
     * Tests that deleting a graph throws a GraphNullException when the graph does not exist.
     */
    @Test
    public void deleteByUuidsGraphNotExistThrowsGraphNullException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String uuid = TestConstants.TEST_ID2;

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(uuid))
                .build();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
    }

    /**
     * Tests that deleting a graph throws a DeleteException when the graph is bound to another user.
     */
    @Test
    public void deleteByUuidsGraphBoundToAnotherUserThrowsDeleteException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String uuid = TestConstants.TEST_ID2;

        final Graph graph = Graph.builder()
                .uuid(uuid)
                .build();

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(uuid))
                .build();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);
        when(graphRepository.getGraphByGraphUuidAndUidcid(uuid, uidcid)).thenReturn(null);

        // Act & Assert
        assertThrows(DeleteException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
    }

    /**
     * Tests that deleting a graph successfully deletes the graph and related graph nodes.
     */
    @Test
    public void deleteByUuidsValidRequestDeletesGraphsAndRelatedGraphNodes() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String graphUuid = TestConstants.TEST_ID2;
        final String nodeUuid = TestConstants.TEST_ID3;

        final Graph graph = Graph.builder()
                .uuid(uidcid)
                .build();

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(graphUuid))
                .build();

        when(graphRepository.getGraphByUuid(graphUuid)).thenReturn(graph);
        when(graphRepository.getGraphByGraphUuidAndUidcid(graphUuid, uidcid)).thenReturn(graphUuid);
        when(graphRepository.getGraphNodeUuidsByGraphUuids(anyList())).thenReturn(Collections.singletonList(nodeUuid));

        doNothing().when(nodeRepository).deleteByUuids(anyList());
        doNothing().when(graphRepository).deleteByUuids(anyList());

        graphService.deleteByUuids(graphDeleteDTO);

        // Act & Assert
        verify(nodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
        verify(graphRepository, times(1)).deleteByUuids(Collections.singletonList(graphUuid));
    }

    /**
     * Tests that updating a graph updates the graph when it exists.
     */
    @Test
    void updateGraphWhenGraphExistsShouldUpdateGraph() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid(uuid);
        graphUpdateDTO.setTitle(TestConstants.TEST_TILE2);
        graphUpdateDTO.setDescription(TestConstants.TEST_DESCRIPTION2);

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(new Graph());

        // Act & Assert
        assertDoesNotThrow(() -> graphService.updateGraph(graphUpdateDTO));

        // Verify
        verify(neo4jService, times(1)).updateGraphByUuid(
                eq(uuid),
                eq(TestConstants.TEST_TILE2),
                eq(TestConstants.TEST_DESCRIPTION2),
                any(String.class)
        );
    }

    /**
     * Tests that updating a graph throws a GraphNullException when the graph does not exist.
     */
    @Test
    void updateGraphGraphNotExistsThrowsGraphNullException() {
        // Arrange
        final GraphUpdateDTO graphUpdateDTO = GraphUpdateDTO.builder()
                .uuid(TestConstants.TEST_ID1)
                .build();
        final String uuid = graphUpdateDTO.getUuid();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.updateGraph(graphUpdateDTO));
    }

    /**
     * Get current time.
     * @return current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
