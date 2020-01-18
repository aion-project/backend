package com.withaion.backend.handlers

import com.withaion.backend.data.*
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Assignment
import com.withaion.backend.models.Event
import com.withaion.backend.models.Group
import com.withaion.backend.utils.EventUtil
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ScheduleHandler(
        private val scheduleRepository: ScheduleRepository,
        private val locationRepository: LocationRepository,
        private val eventRepository: EventRepository
) {

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(ScheduleNewDto::class.java).flatMap { scheduleNewDto ->
                Mono.zip(
                        locationRepository.findById(scheduleNewDto.location),
                        eventRepository.findById(scheduleNewDto.event)
                ).flatMap {
                    scheduleRepository.save(scheduleNewDto.toSchedule(it.t1, it.t2))
                }
            }.map { "Schedule created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setLocation(request: ServerRequest) = ServerResponse.ok().body(
            scheduleRepository.findById(request.pathVariable("id")).flatMap { schedule ->
                request.bodyToMono(IdDto::class.java)
                        .flatMap { locationRepository.findById(it.id) }
                        .map { schedule.copy(location = it) }
                        .flatMap { scheduleRepository.save(it) }
            }.map { "Location added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeLocation(request: ServerRequest) = ServerResponse.ok().body(
            scheduleRepository.findById(request.pathVariable("id"))
                    .map { schedule -> schedule.copy(location = null) }
                    .flatMap { scheduleRepository.save(it) }
                    .map { "Location removed successfully".toResponse() },
            ResponseDto::class.java
    )
}