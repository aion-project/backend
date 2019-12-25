package com.withaion.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "subject")
data class Subject(
        @Id val id: String? = null,
        val name: String,
        val description: String?
)