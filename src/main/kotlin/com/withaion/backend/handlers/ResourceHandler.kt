package com.withaion.backend.handlers

import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.dto.ResourceNewDto
import com.withaion.backend.dto.ResourceUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Resource
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class ResourceHandler(
        private val resourceRepository: ResourceRepository
) {

  fun get(request: ServerRequest) = ServerResponse.ok().body(
          resourceRepository.findById(request.pathVariable("id")),
          Resource::class.java
  )

  fun getAll() = ServerResponse.ok().body(
          resourceRepository.findAll(),
          Resource::class.java
  )

  fun create(request: ServerRequest) = ServerResponse.ok().body(
          request.bodyToMono(ResourceNewDto::class.java)
                  .flatMap { resourceRepository.save(it.toResource()) }
                  .map { "Resource created successfully".toResponse() },
          ResponseDto::class.java
  )

  fun delete(request: ServerRequest) = ServerResponse.ok().body(
          resourceRepository.deleteById(request.pathVariable("id")).thenReturn(true)
                  .map { "Resouce deleted successfully".toResponse() },
          ResponseDto::class.java
  )

  fun update(request: ServerRequest) = ServerResponse.ok().body(
          request.bodyToMono(ResourceUpdateDto::class.java)
                  .flatMap { updateResource ->
                    resourceRepository.findById(request.pathVariable("id"))
                            .flatMap { resourceRepository.save(updateResource.toUpdatedLocation(it)) }
                  }.map { "Resource updated successfully".toResponse() },
          ResponseDto::class.java
  )

}
