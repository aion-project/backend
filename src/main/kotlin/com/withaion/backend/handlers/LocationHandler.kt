package com.withaion.backend.handlers

import com.withaion.backend.data.LocationRepository
import com.withaion.backend.dto.LocationNewDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class LocationHandler(
        private val locationRepository: LocationRepository
) {

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(LocationNewDto::class.java)
                    .flatMap {
                        locationRepository.save(it.toLocation())
                    }.map { "Location created successfully".toResponse() },
            ResponseDto::class.java
    )

}