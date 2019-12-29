package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "assignment")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Assignment(
        @Id val id: String? = null,
        @DBRef val user: User,
        @DBRef val event: Event,
        val role: String
)
