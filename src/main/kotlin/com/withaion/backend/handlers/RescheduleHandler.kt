package com.withaion.backend.handlers

import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.RescheduleRepository
import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.ResourceNewDto
import com.withaion.backend.dto.ResourceUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Location
import com.withaion.backend.models.Reschedule
import com.withaion.backend.models.RescheduleStatus
import com.withaion.backend.models.Resource
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


class RescheduleHandler(
        private val rescheduleRepository: RescheduleRepository,
        private val userRepository: UserRepository
) {

    fun getMine(request: ServerRequest) = ServerResponse.ok().body(
            Flux.from(request.principal().flatMap { userRepository.findByEmail(it.name) }).flatMap { user ->
                rescheduleRepository.findAllByRequestedBy(user)
            },
            Reschedule::class.java
    )

    fun getPending() = ServerResponse.ok().body(
            rescheduleRepository.findAllByStatus(RescheduleStatus.PENDING),
            Reschedule::class.java
    )

}
