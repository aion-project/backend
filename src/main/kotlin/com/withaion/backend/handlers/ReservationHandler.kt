package com.withaion.backend.handlers

import com.okta.sdk.resource.ResourceException
import com.withaion.backend.data.RescheduleRepository
import com.withaion.backend.data.ReservationRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.exceptions.InvalidStateException
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Reschedule
import com.withaion.backend.models.RescheduleStatus
import com.withaion.backend.models.Reservation
import com.withaion.backend.models.ReservationStatus
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


class ReservationHandler(
        private val reservationRepository: ReservationRepository,
        private val userRepository: UserRepository
) {

    fun getMine(request: ServerRequest) = ServerResponse.ok().body(
            Flux.from(request.principal().flatMap { userRepository.findByEmail(it.name) }).flatMap { user ->
                reservationRepository.findAllByRequestedBy(user)
            },
            Reservation::class.java
    )

    fun getPending() = ServerResponse.ok().body(
            reservationRepository.findAllByStatus(ReservationStatus.PENDING),
            Reservation::class.java
    )

    fun getReviewed() = ServerResponse.ok().body(
            reservationRepository.findAllByStatusIn(listOf(ReservationStatus.REVIEWED)),
            Reservation::class.java
    )

    fun getClosed() = ServerResponse.ok().body(
            reservationRepository.findAllByStatusIn(listOf(ReservationStatus.ACCEPTED, ReservationStatus.DECLINED)),
            Reservation::class.java
    )

    fun accept(request: ServerRequest) = reservationRepository.findById(request.pathVariable("id")).flatMap {
        if (it.status != ReservationStatus.REVIEWED)
            return@flatMap Mono.error<InvalidStateException>(InvalidStateException())
        reservationRepository.save(it.copy(status = ReservationStatus.ACCEPTED))
    }.flatMap {
        ServerResponse.ok().syncBody("Reschedule reviewed successfully".toResponse())
    }.onErrorResume {
        when (it) {
            is InvalidStateException -> ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody(it.response)
            else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
        }
    }

    fun review(request: ServerRequest) = reservationRepository.findById(request.pathVariable("id")).flatMap {
        if (it.status != ReservationStatus.PENDING)
            return@flatMap Mono.error<InvalidStateException>(InvalidStateException())
        reservationRepository.save(it.copy(status = ReservationStatus.REVIEWED))
    }.flatMap {
        ServerResponse.ok().syncBody("Reschedule reviewed successfully".toResponse())
    }.onErrorResume {
        when (it) {
            is InvalidStateException -> ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody(it.response)
            else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
        }
    }


    fun decline(request: ServerRequest) = reservationRepository.findById(request.pathVariable("id")).flatMap {
        if (it.status != ReservationStatus.REVIEWED)
            return@flatMap Mono.error<InvalidStateException>(InvalidStateException())
        reservationRepository.save(it.copy(status = ReservationStatus.DECLINED))
    }.flatMap {
        ServerResponse.ok().syncBody("Reschedule reviewed successfully".toResponse())
    }.onErrorResume {
        when (it) {
            is InvalidStateException -> ServerResponse.status(HttpStatus.BAD_REQUEST).syncBody(it.response)
            else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
        }
    }


}
