package com.withaion.backend.handlers

import com.withaion.backend.data.*
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Assignment
import com.withaion.backend.models.Event
import com.withaion.backend.models.Group
import com.withaion.backend.services.OktaService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.lang.Exception

class EventHandler(
        private val eventRepository: EventRepository,
        private val subjectRepository: SubjectRepository,
        private val groupRepository: GroupRepository,
        private val locationRepository: LocationRepository,
        private val userRepository: UserRepository,
        private val assignmentRepository: AssignmentRepository
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

    fun getAssignments(request: ServerRequest) = ServerResponse.ok().body(
            assignmentRepository.findAllByEvent_Id(request.pathVariable("id")),
            Assignment::class.java
    )

    fun addAssignment(request: ServerRequest) = request.bodyToMono(AssignUserDto::class.java).flatMap { req ->
        Mono.zip(
                userRepository.findByEmail(req.email),
                eventRepository.findById(request.pathVariable("id"))
        ).map {
            val role = it.t1.roles.firstOrNull { role -> role.name == req.role }

            if (role != null) {
                Assignment(user = it.t1, event = it.t2, role = req.role)
            } else {
                throw Exception("no role")
            }
        }.flatMap {
            assignmentRepository.save(it)
        }.flatMap {
            ServerResponse.ok().syncBody("User assigned successfully".toResponse())
        }.onErrorResume {
            if (it is Exception && it.message == "no role") {
                ServerResponse.badRequest().syncBody("User doesn\'t have given role".toResponse())
            } else {
                it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
            }
        }
    }

    fun removeAssignment(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(IdDto::class.java).flatMap { req ->
                assignmentRepository.deleteById(req.id)
            }.map {
                "User removed successfully".toResponse()
            },
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
                    .map { event -> event.copy(location = null) }
                    .flatMap { eventRepository.save(it) }
                    .map { "Location removed successfully".toResponse() },
            ResponseDto::class.java
    )
}