package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "subject")
data class Subject(
        @Id val id: String? = null,
        val name: String,
        val description: String?,
        @DBRef(lazy = true) @JsonBackReference val groups: List<Group> = listOf()
)