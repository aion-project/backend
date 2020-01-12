package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.Reschedule
import com.withaion.backend.models.User
import java.time.LocalDateTime

class RescheduleRequestDto(
        val oldDateTime: LocalDateTime,
        val newDateTime: LocalDateTime
) {
    fun toReschedule(event: Event, requestedBy: User): Reschedule {
        return Reschedule(event = event, oldDateTime = oldDateTime, newDateTime = newDateTime, requestedBy = requestedBy)
    }
}