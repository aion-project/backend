package com.withaion.backend.handlers

import com.withaion.backend.data.LocationRepository
import com.withaion.backend.dto.LocationNewDto
import com.withaion.backend.dto.LocationUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Location
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class LocationHandler(
        private val locationRepository: LocationRepository
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
            locationRepository.deleteById(request.pathVariable("id")).thenReturn(true)
                    .map { "Location deleted successfully".toResponse() },
            ResponseDto::class.java
    )

}