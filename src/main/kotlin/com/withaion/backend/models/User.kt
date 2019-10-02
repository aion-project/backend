package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
        @Id val id: String? = null,
        val firstName: String,
        val lastName: String,
        val email: String,
        val enabled: Boolean = true,
        val active: Boolean? = false,
        val roles: List<Role>? = null,
        val avatarUrl: String? = null,
        val thumbnailUrl: String? = null,
        val bio: String? = null
)