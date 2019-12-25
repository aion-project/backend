package com.withaion.backend.handlers

import com.withaion.backend.data.EventRepository
import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.SubjectRepository
import com.withaion.backend.dto.EventNewDto
import com.withaion.backend.dto.EventUpdateDto
import com.withaion.backend.dto.IdDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Event
import com.withaion.backend.models.SubjectRef
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class EventHandler(
        private val eventRepository: EventRepository,
        private val subjectRepository: SubjectRepository,
        private val locationRepository: LocationRepository
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

    fun setSubject(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")).flatMap { event ->
                request.bodyToMono(IdDto::class.java)
                        .flatMap { subjectRepository.findById(it.id).map { subject -> SubjectRef(subject) } }
                        .map { event.copy(subject = it) }
                        .flatMap { eventRepository.save(it) }
            }.map { "Subject added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeSubject(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id"))
                    .map { event -> event.copy(subject = null)}
                    .flatMap { eventRepository.save(it) }
                    .map { "Subject removed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setLocation(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")).flatMap { event ->
                request.bodyToMono(IdDto::class.java)
                        .flatMap { locationRepository.findById(it.id) }
                        .map { event.copy(location = it) }
                        .flatMap { eventRepository.save(it) }
            }.map { "Location added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeLocation(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id"))
                    .map { event -> event.copy(location = null)}
                    .flatMap { eventRepository.save(it) }
                    .map { "Location removed successfully".toResponse() },
            ResponseDto::class.java
    )
}