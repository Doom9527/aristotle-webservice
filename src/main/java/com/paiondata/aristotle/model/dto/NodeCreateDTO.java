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
package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Data Transfer Object (DTO) for creating nodes within a graph.
 *
 * This DTO is used to encapsulate the data required for creating nodes and their relations within a specific graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCreateDTO extends BaseEntity {

    /**
     * The UUID of the graph where the nodes will be created.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    /**
     * The list of nodes to be created within the graph.
     *
     * @see NodeDTO
     */
    @Valid
    private List<NodeDTO> graphNodeDTO;

    /**
     * The list of relations between nodes within the graph.
     *
     * @see NodeRelationDTO
     */
    @Valid
    private List<NodeRelationDTO> graphNodeRelationDTO;
}
