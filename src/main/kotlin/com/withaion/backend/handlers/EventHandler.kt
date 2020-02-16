package com.withaion.backend.handlers

import com.mongodb.DBRef
import com.withaion.backend.data.*
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Event
import com.withaion.backend.models.Group
import com.withaion.backend.models.Schedule
import com.withaion.backend.utils.EventUtil
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class EventHandler(
        private val eventRepository: EventRepository,
        private val subjectRepository: SubjectRepository,
        private val groupRepository: GroupRepository,
        private val scheduleRepository: ScheduleRepository,
        private val userRepository: UserRepository,
        private val rescheduleRepository: RescheduleRepository,
        private val mongoTemplate: ReactiveMongoTemplate
) {

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")),
            Event::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            eventRepository.findAll(),
            Event::class.java
    )

    fun getMine(request: ServerRequest) = ServerResponse.ok().body(
            Flux.from(
                    request.principal().flatMap { userRepository.findByEmail(it.name) }
            ).map { user ->
                val events = mutableListOf<Event>()
                user.groups.forEach {
                    events.addAll(it.events)
                }
                user.schedules.forEach {
                    it?.event?.let { it1 -> events.add(it1) }
                }
                events
            }.flatMap { events ->
                Flux.fromIterable(events.flatMap {
                    EventUtil.expandEvents(it.schedules)
                })
            },
            ScheduledEvent::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(EventNewDto::class.java)
                    .flatMap {
                        eventRepository.save(it.toEvent())
                    }
                    .map { "Event created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")).flatMap { event ->
                Mono.zip(
                        eventRepository.delete(event).thenReturn(true),
                        mongoTemplate.remove(Query.query(Criteria.where("event").`is`(event.id)), Schedule::class.java).thenReturn(true)
                )
            }.map { "Event deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(EventUpdateDto::class.java)
                    .flatMap { event ->
                        eventRepository.findById(request.pathVariable("id"))
                                .flatMap { eventRepository.save(event.toUpdatedEvent(it)) }
                    }.map { "Event updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setSubject(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id")).flatMap { event ->
                request.bodyToMono(IdDto::class.java)
                        .flatMap { subjectRepository.findById(it.id) }
                        .map { event.copy(subject = it) }
                        .flatMap { eventRepository.save(it) }
            }.map { "Subject added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeSubject(request: ServerRequest) = ServerResponse.ok().body(
            eventRepository.findById(request.pathVariable("id"))
                    .map { event -> event.copy(subject = null) }
                    .flatMap { eventRepository.save(it) }
                    .map { "Subject removed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun addGroup(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(IdDto::class.java).flatMap { req ->
                Mono.zip(
                        groupRepository.findById(req.id),
                        eventRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val groups: ArrayList<Group> = ArrayList(it.t2.groups)
                    groups.add(it.t1)
                    val events: ArrayList<Event> = ArrayList(it.t1.events)
                    events.add(it.t2)

                    Mono.zip(
                            groupRepository.save(it.t1.copy(events = events)),
                            eventRepository.save(it.t2.copy(groups = groups))
                    )
                }.map { "Group added successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun removeGroup(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(IdDto::class.java).flatMap { req ->
                Mono.zip(
                        groupRepository.findById(req.id),
                        eventRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val groups: ArrayList<Group> = ArrayList(it.t2.groups)
                    groups.remove(it.t1)
                    val events: ArrayList<Event> = ArrayList(it.t1.events)
                    events.remove(it.t2)

                    Mono.zip(
                            groupRepository.save(it.t1.copy(events = events)),
                            eventRepository.save(it.t2.copy(groups = groups))
                    )
                }.map { "Group removed successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun reschedule(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(RescheduleRequestDto::class.java).flatMap { rescheduleRequest ->
                Mono.zip(
                        request.principal().flatMap { principal -> userRepository.findByEmail(principal.name) },
                        eventRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    val user = it.t1
                    val event = it.t2

                    rescheduleRepository.save(rescheduleRequest.toReschedule(event, user))
                }
            }.map { "Reschedule request successful".toResponse() },
            ResponseDto::class.java
    )
}