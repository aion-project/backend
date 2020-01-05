package com.withaion.backend.handlers

import com.withaion.backend.data.EventRepository
import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.dto.LocationChangeResourceDto
import com.withaion.backend.dto.LocationNewDto
import com.withaion.backend.dto.LocationUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import com.withaion.backend.models.Resource
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class LocationHandler(
        private val locationRepository: LocationRepository,
        private val resourceRepository: ResourceRepository,
        private val eventRepository: EventRepository,
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
            eventRepository.findAllByLocation_Id(request.pathVariable("id")),
            Event::class.java
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