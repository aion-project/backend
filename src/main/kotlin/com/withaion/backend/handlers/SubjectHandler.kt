package com.withaion.backend.handlers

import com.withaion.backend.data.SubjectRepository
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.dto.SubjectNewDto
import com.withaion.backend.dto.SubjectUpdateDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Group
import com.withaion.backend.models.Subject
import com.withaion.backend.models.User
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class SubjectHandler(
        private val subjectRepository: SubjectRepository,
        private val mongoTemplate: ReactiveMongoTemplate
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
            subjectRepository.findById(request.pathVariable("id")).flatMap {
                subjectRepository.delete(it).thenReturn(true).map { "Subject deleted successfully".toResponse() }
            },
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
