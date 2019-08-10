package com.withaion.backend.data

import com.withaion.backend.models.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository: ReactiveMongoRepository<User, String> {

    fun findByUsername(username: String): Mono<User>

}