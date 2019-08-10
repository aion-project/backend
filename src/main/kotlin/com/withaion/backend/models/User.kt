package com.withaion.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
class User(
        @Id var id: String?,
        val username: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        val password: String
)