package com.withaion.backend.handlers

import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.RescheduleRepository
import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.ResourceNewDto
import com.withaion.backend.dto.ResourceUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.exceptions.InvalidStateException
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.*
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
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

    fun getReviewed() = ServerResponse.ok().body(
            rescheduleRepository.findAllByStatusIn(listOf(RescheduleStatus.ACCEPTED, RescheduleStatus.DECLINED)),
            Reschedule::class.java
    )

    fun delete(request: ServerRequest) = rescheduleRepository.findById(request.pathVariable("id")).flatMap {
        if (it.status == RescheduleStatus.ACCEPTED)
            return@flatMap Mono.error<InvalidStateException>(InvalidStateException())

        rescheduleRepository.delete(it).thenReturn(true)
    }.flatMap {
        ServerResponse.ok().syncBody("Reschedule deleted successfully".toResponse())
    }.onErrorResume {
        when (it) {
            is InvalidStateException -> ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody(it.response)
            else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
        }
    }

    fun accept(request: ServerRequest) = ServerResponse.ok().body(
            rescheduleRepository.findById(request.pathVariable("id")).flatMap {
                // TODO - Implement reschedule logic

                rescheduleRepository.save(it.copy(status = RescheduleStatus.ACCEPTED))
            }.map { "Reschedule approved successfully".toResponse() },
            ResponseDto::class.java
    )

    fun decline(request: ServerRequest) = ServerResponse.ok().body(
            rescheduleRepository.findById(request.pathVariable("id")).flatMap {
                rescheduleRepository.save(it.copy(status = RescheduleStatus.DECLINED))
            }.map { "Reschedule declined successfully".toResponse() },
            ResponseDto::class.java
    )

}
