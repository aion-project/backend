package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class ReservationStatus {
    ACCEPTED, REVIEWED, DECLINED, PENDING
}

@Document(collection = "reservation")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Reservation(
        @Id val id: String? = null,
        val event: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val status: ReservationStatus = ReservationStatus.PENDING,
        @DBRef val location: Location? = null,
        @DBRef val requestedBy: User? = null
) {

    fun toEvent(): Event {
        return Event(name = event, description = description)
    }

    fun toSchedule(location: Location, event: Event): Schedule {
        return Schedule(startDateTime = startDateTime, endDateTime = endDateTime, until = null, repeatType = RepeatType.NONE, location = location, event = event)
    }
}