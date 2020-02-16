package com.withaion.backend.handlers

import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.data.ScheduleRepository
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Location
import com.withaion.backend.models.Resource
import com.withaion.backend.utils.EventUtil
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class LocationHandler(
        private val locationRepository: LocationRepository,
        private val resourceRepository: ResourceRepository,
        private val scheduleRepository: ScheduleRepository,
        private val mongoTemplate: ReactiveMongoTemplate
) {

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            locationRepository.findById(request.pathVariable("id")),
            Location::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            locationRepository.findAll(),
            Location::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(LocationNewDto::class.java)
                    .flatMap {
                        locationRepository.save(it.toLocation())
                    }.map { "Location created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(LocationUpdateDto::class.java)
                    .flatMap { updateLocation ->
                        locationRepository.findById(request.pathVariable("id"))
                                .flatMap { locationRepository.save(updateLocation.toUpdatedLocation(it)) }
                    }.map { "Location updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            locationRepository.findById(request.pathVariable("id")).flatMap {
                val update: Update = Update().pull("locations", it)

                Mono.zip(
                        mongoTemplate.upsert(Query(), update, Resource::class.java),
                        locationRepository.delete(it).thenReturn(true)
                ).map { "Location deleted successfully".toResponse() }
            }
            ,
            ResponseDto::class.java
    )

    fun events(request: ServerRequest) = ServerResponse.ok().body(
            scheduleRepository.findAllByLocation_Id(request.pathVariable("id"))
                    .flatMap { schedule -> Flux.fromIterable(EventUtil.expandEvents(schedule)) },
            ScheduledEvent::class.java
    )

    fun available(request: ServerRequest) = ServerResponse.ok().body(
            locationRepository.findAll().filterWhen { location ->
                val timeParam = request.queryParam("time") as Optional
                if (timeParam.isEmpty()) return@filterWhen Mono.just(false)

                val time = LocalDateTime.parse(timeParam.get().substring(0, 19))
                scheduleRepository.findAllByLocation_Id(location.id!!)
                        .flatMap { schedule -> Flux.fromIterable(EventUtil.expandEvents(schedule)) }
                        .any { event ->
                            (time.isAfter(event.startDateTime) || time.isEqual(event.startDateTime)) &&
                                    (time.isBefore(event.endDateTime) || time.isEqual(event.endDateTime))
                        }.map {
                            !it
                        }
            }
            ,
            Location::class.java
    )

    fun addResource(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(LocationChangeResourceDto::class.java).flatMap { req ->
                Mono.zip(
                        resourceRepository.findById(req.resource),
                        locationRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val resources: ArrayList<Resource> = ArrayList(it.t2.resources)
                    resources.add(it.t1)
                    val locations: ArrayList<Location> = ArrayList(it.t1.locations)
                    locations.add(it.t2)

                    Mono.zip(
                            resourceRepository.save(it.t1.copy(locations = locations)),
                            locationRepository.save(it.t2.copy(resources = resources))
                    )
                }.map { "Resource added successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun removeResource(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(LocationChangeResourceDto::class.java).flatMap { req ->
                Mono.zip(
                        resourceRepository.findById(req.resource),
                        locationRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val resources: ArrayList<Resource> = ArrayList(it.t2.resources)
                    resources.remove(it.t1)
                    val locations: ArrayList<Location> = ArrayList(it.t1.locations)
                    locations.remove(it.t2)

                    Mono.zip(
                            resourceRepository.save(it.t1.copy(locations = locations)),
                            locationRepository.save(it.t2.copy(resources = resources))
                    )
                }.map { "Resource removed successfully".toResponse() }
            },
            ResponseDto::class.java
    )

}