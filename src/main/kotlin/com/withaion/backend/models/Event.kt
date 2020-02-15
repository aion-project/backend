package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.LocalTime

@Document(collection = "event")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Event(
        @Id val id: String? = null,
        val name: String,
        val description: String?,
        @DBRef val subject: Subject? = null,
        @DBRef(lazy = true) @JsonManagedReference val schedules: List<Schedule> = listOf(),
        @DBRef(lazy = true) @JsonManagedReference val groups: List<Group> = listOf(),
        @DBRef val createdBy: User? = null
)