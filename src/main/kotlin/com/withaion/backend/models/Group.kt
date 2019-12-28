package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "group")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Group(
        @Id val id: String? = null,
        val name: String,
        val description: String,
        @DBRef(lazy = true) @JsonBackReference val users: List<User> = listOf(),
        @DBRef(lazy = true) @JsonBackReference val events: List<Event> = listOf()
)