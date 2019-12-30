package com.withaion.backend.data

import com.withaion.backend.models.Assignment
import com.withaion.backend.models.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AssignmentRepository : ReactiveMongoRepository<Assignment, String> {

    fun findAllByEvent_Id(eventId: String): Flux<Assignment>
    fun findAllByUser_Id(userId: String): Flux<Assignment>
    fun findAllByUser_Email(userEmail: String): Flux<Assignment>
    fun deleteByUserEquals(user: User): Mono<Void>

}
