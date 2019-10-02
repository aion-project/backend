package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "role")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Role(
        @Id val id: String,
        val name: String,
        val description: String?
)