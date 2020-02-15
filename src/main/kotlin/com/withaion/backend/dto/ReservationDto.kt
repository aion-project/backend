package com.withaion.backend.dto

import com.withaion.backend.models.*
import java.time.LocalDateTime

class ReservationNewDto(
        val event: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val location: String
        ) {
    fun toReservation(location: Location, requestedBy: User): Reservation {
        return Reservation(
                event = event,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                requestedBy = requestedBy
        )
    }
}
