package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
        val id: String? = null,
        val firstName: String,
        val lastName: String,
        val email: String,
        val enabled: Boolean = true,
        val active: Boolean? = false,
        val roles: List<Role> = listOf(),
        val avatarUrl: String? = null,
        val thumbnailUrl: String? = null,
        val bio: String? = null,
        @DBRef(lazy = true) @JsonManagedReference val groups: List<Group> = listOf(),
        @DBRef val location: Location? = null
)