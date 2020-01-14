package com.withaion.backend.dto

import com.withaion.backend.models.*
import java.time.LocalDateTime

class RescheduleRequestDto(
        val oldDateTime: LocalDateTime,
        val newDateTime: LocalDateTime,
        val type: RescheduleType
) {
    fun toReschedule(event: Event, requestedBy: User): Reschedule {
        return Reschedule(event = event, oldDateTime = oldDateTime, newDateTime = newDateTime, type = type, requestedBy = requestedBy)
    }
}