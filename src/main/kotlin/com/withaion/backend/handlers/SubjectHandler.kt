package com.withaion.backend.handlers

import com.withaion.backend.data.SubjectRepository
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.dto.SubjectNewDto
import com.withaion.backend.dto.SubjectUpdateDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Subject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class SubjectHandler(
        private val subjectRepository: SubjectRepository
) {

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            subjectRepository.findById(request.pathVariable("id")),
            Subject::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            subjectRepository.findAll(),
            Subject::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(SubjectNewDto::class.java)
                    .flatMap { subjectRepository.save(it.toSubject()) }
                    .map { "Subject created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            subjectRepository.deleteById(request.pathVariable("id")).thenReturn(true)
                    .map { "Subject deleted successfully".toResponse() }
            ,
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(SubjectUpdateDto::class.java)
                    .flatMap { updateResource ->
                        subjectRepository.findById(request.pathVariable("id"))
                                .flatMap { subjectRepository.save(updateResource.toUpdatedSubject(it)) }
                    }.map { "Subject updated successfully".toResponse() },
            ResponseDto::class.java
    )

}
