package com.withaion.backend.data

import com.withaion.backend.models.Assignment
import com.withaion.backend.models.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AssignmentRepository : ReactiveMongoRepository<Assignment, String> {

    fun findAllByEvent_Id(event_id: String): Flux<Assignment>
    fun deleteAllByUser(user: User): Mono<Void>

}
