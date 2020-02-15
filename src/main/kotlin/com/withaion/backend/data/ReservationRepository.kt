package com.withaion.backend.data

import com.withaion.backend.models.*
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ReservationRepository : ReactiveMongoRepository<Reservation, String> {

    fun findAllByRequestedBy(user: User): Flux<Reservation>
    fun findAllByStatus(status: ReservationStatus): Flux<Reservation>
    fun findAllByStatusIn(status: List<ReservationStatus>): Flux<Reservation>

}
