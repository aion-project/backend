package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class RepeatType {
    NONE, DAILY, WEEKLY
}

@Document(collection = "schedule")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Schedule(
        @Id val id: String? = null,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val until: LocalDateTime? = null,
        val repeatType: RepeatType = RepeatType.NONE,
        @DBRef @JsonBackReference val event: Event? = null,
        @DBRef val location: Location? = null
)