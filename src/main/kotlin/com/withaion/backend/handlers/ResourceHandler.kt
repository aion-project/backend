package com.withaion.backend.handlers

import com.withaion.backend.data.ResourceRepository
import com.withaion.backend.dto.ResourceNewDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Resource
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class ResourceHandler(
        private val resourceRepository: ResourceRepository
) {

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

}
