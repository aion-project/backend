package com.withaion.backend.handlers

import com.withaion.backend.data.*
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Schedule
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ScheduleHandler(
        private val scheduleRepository: ScheduleRepository,
        private val locationRepository: LocationRepository,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository
) {

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(ScheduleNewDto::class.java).flatMap { scheduleNewDto ->
                Mono.zip(
                        locationRepository.findById(scheduleNewDto.location),
                        eventRepository.findById(scheduleNewDto.event)
                ).flatMap { locationEvent ->
                    val schedules = ArrayList<Schedule>(locationEvent.t2.schedules)
                    scheduleRepository.save(scheduleNewDto.toSchedule(locationEvent.t1, locationEvent.t2)).flatMap {
                        schedules.add(it)
                        eventRepository.save(locationEvent.t2.copy(schedules = schedules))
                    }
                }
            }.map { "Schedule created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            scheduleRepository.findById(request.pathVariable("id")).flatMap { schedule ->
                eventRepository.findById(schedule.event!!.id!!).flatMap { event ->
                    val schedules = ArrayList<Schedule>(event.schedules)
                    schedules.remove(schedule)

                    eventRepository.save(event.copy(schedules = schedules)).flatMap {
                        scheduleRepository.delete(schedule).thenReturn(true)
                    }
                }
            }.map { "Schedule removed successfully".toResponse() },
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

    fun addUser(request: ServerRequest) = request.bodyToMono(AssignUserDto::class.java).flatMap { req ->
        Mono.zip(
                userRepository.findByEmail(req.email),
                scheduleRepository.findById(request.pathVariable("id"))
        ).flatMap {
            val users = ArrayList(it.t2.users)
            users.add(it.t1)
            val schedules = ArrayList(it.t1.schedules)
            schedules.add(it.t2)

            Mono.zip(
                    userRepository.save(it.t1.copy(schedules = schedules)),
                    scheduleRepository.save(it.t2.copy(users = users))
            )
        }.flatMap {
            ServerResponse.ok().syncBody("User assigned successfully".toResponse())
        }
    }

    fun removeUser(request: ServerRequest) = request.bodyToMono(AssignUserDto::class.java).flatMap { req ->
        Mono.zip(
                userRepository.findByEmail(req.email),
                scheduleRepository.findById(request.pathVariable("id"))
        ).flatMap {
            val users = ArrayList(it.t2.users)
            users.remove(it.t1)
            val schedules = ArrayList(it.t1.schedules)
            schedules.remove(it.t2)

            Mono.zip(
                    userRepository.save(it.t1.copy(schedules = schedules)),
                    scheduleRepository.save(it.t2.copy(users = users))
            )
        }.flatMap {
            ServerResponse.ok().syncBody("User removed successfully".toResponse())
        }
    }

}