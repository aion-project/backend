package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class RepeatType {
    NONE, DAILY, WEEKLY, MONTHLY
}

@Document(collection = "event")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Event(
        @Id val id: String? = null,
        val name: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val repeat: RepeatType = RepeatType.NONE,
        @DBRef val subject: Subject? = null,
        @DBRef val location: Location? = null,
        @DBRef val createdBy: User? = null
)