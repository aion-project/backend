package com.withaion.backend.data

import com.withaion.backend.models.Reschedule
import com.withaion.backend.models.RescheduleStatus
import com.withaion.backend.models.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface RescheduleRepository : ReactiveMongoRepository<Reschedule, String> {

    fun findAllByRequestedBy(user: User): Flux<Reschedule>
    fun findAllByStatus(status: RescheduleStatus): Flux<Reschedule>

}
