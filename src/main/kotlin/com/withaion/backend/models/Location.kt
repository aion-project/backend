package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "location")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Location(
        @Id val id: String? = null,
        val name: String,
        val level: String,
        val description: String? = null,
        val quantity: Int = 0,
        val ac: Boolean = false,
        @DBRef(lazy = true) @JsonManagedReference val resources: List<Resource> = listOf()
)
