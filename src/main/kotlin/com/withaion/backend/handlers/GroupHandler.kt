package com.withaion.backend.handlers

import com.withaion.backend.data.GroupRepository
import com.withaion.backend.dto.GroupNewDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Group
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class GroupHandler(
        private val groupRepository: GroupRepository
) {

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            groupRepository.findById(request.pathVariable("id")),
            Group::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            groupRepository.findAll(),
            Group::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(GroupNewDto::class.java)
                    .flatMap { groupRepository.save(it.toGroup()) }
                    .map { "Group created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            groupRepository.deleteById(request.pathVariable("id")).thenReturn(true)
                    .map { "Group deleted successfully".toResponse() },
            ResponseDto::class.java
    )
}