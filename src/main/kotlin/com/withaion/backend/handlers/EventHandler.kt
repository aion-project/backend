package com.withaion.backend.handlers

import com.withaion.backend.data.EventRepository
import com.withaion.backend.dto.EventNewDto
import com.withaion.backend.dto.EventUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Event
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class EventHandler(
        private val eventRepository: EventRepository
) {

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")),
            Event::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            eventRepository.findAll(),
            Event::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(EventNewDto::class.java)
                    .flatMap { eventRepository.save(it.toEvent()) }
                    .map { "Event created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.deleteById(request.pathVariable("id")).thenReturn(true)
                    .map { "Event deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(EventUpdateDto::class.java)
                    .flatMap { updateGroup ->
                        eventRepository.findById(request.pathVariable("id"))
                                .flatMap { eventRepository.save(updateGroup.toUpdatedEvent(it)) }
                    }.map { "Event updated successfully".toResponse() },
            ResponseDto::class.java
    )
}