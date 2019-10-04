package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "location")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Location(
        @Id val id: String? = null,
        val name: String,
        val level: String,
        val description: String? = null
)