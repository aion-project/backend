package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class RepeatType {
    NONE, DAILY, WEEKLY, MONTHLY
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Assignment(
        @DBRef val user: User,
        @DBRef val role: Role
)

@Document(collection = "event")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Event(
        @Id val id: String? = null,
        val name: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val repeat: RepeatType = RepeatType.NONE,
        val assignments: List<Assignment> = listOf(),
        @DBRef val subject: Subject? = null,
        @DBRef(lazy = true) val groups: List<Group> = listOf(),
        @DBRef val location: Location? = null,
        @DBRef val createdBy: User? = null
)