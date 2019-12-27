package com.withaion.backend.handlers

import com.withaion.backend.data.GroupRepository
import com.withaion.backend.data.SubjectRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.*
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class GroupHandler(
        private val groupRepository: GroupRepository,
        private val userRepository: UserRepository,
        private val subjectRepository: SubjectRepository,
        private val mongoTemplate: ReactiveMongoTemplate
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
            groupRepository.findById(request.pathVariable("id")).flatMap {
                val update: Update = Update().pull("groups", it)

                Mono.zip(
                        mongoTemplate.upsert(Query(), update, User::class.java),
                        mongoTemplate.upsert(Query(), update, Subject::class.java),
                        groupRepository.delete(it).thenReturn(true)
                ).map { "Group deleted successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(GroupUpdateDto::class.java)
                    .flatMap { updateGroup ->
                        groupRepository.findById(request.pathVariable("id"))
                                .flatMap { groupRepository.save(updateGroup.toUpdatedGroup(it)) }
                    }.map { "Group updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun addUser(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(GroupChangeUserDto::class.java).flatMap { req ->
                Mono.zip(
                        userRepository.findById(req.user),
                        groupRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val users: ArrayList<User> = ArrayList(it.t2.users)
                    users.add(it.t1)
                    val groups: ArrayList<Group> = ArrayList(it.t1.groups)
                    groups.add(it.t2)

                    Mono.zip(
                            userRepository.save(it.t1.copy(groups = groups)),
                            groupRepository.save(it.t2.copy(users = users))
                    )
                }.map { "User added successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun removeUser(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(GroupChangeUserDto::class.java).flatMap { req ->
                Mono.zip(
                        userRepository.findById(req.user),
                        groupRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val users: ArrayList<User> = ArrayList(it.t2.users)
                    users.remove(it.t1)
                    val groups: ArrayList<Group> = ArrayList(it.t1.groups)
                    groups.remove(it.t2)

                    Mono.zip(
                            userRepository.save(it.t1.copy(groups = groups)),
                            groupRepository.save(it.t2.copy(users = users))
                    )
                }.map { "User removed successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun addSubject(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(IdDto::class.java).flatMap { req ->
                Mono.zip(
                        subjectRepository.findById(req.id),
                        groupRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val subjects: ArrayList<Subject> = ArrayList(it.t2.subjects)
                    subjects.add(it.t1)
                    val groups: ArrayList<Group> = ArrayList(it.t1.groups)
                    groups.add(it.t2)

                    Mono.zip(
                            subjectRepository.save(it.t1.copy(groups = groups)),
                            groupRepository.save(it.t2.copy(subjects = subjects))
                    )
                }.map { "Subject added successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    fun removeSubject(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(IdDto::class.java).flatMap { req ->
                Mono.zip(
                        subjectRepository.findById(req.id),
                        groupRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    // Update objects
                    val subjects: ArrayList<Subject> = ArrayList(it.t2.subjects)
                    subjects.remove(it.t1)
                    val groups: ArrayList<Group> = ArrayList(it.t1.groups)
                    groups.remove(it.t2)

                    Mono.zip(
                            subjectRepository.save(it.t1.copy(groups = groups)),
                            groupRepository.save(it.t2.copy(subjects = subjects))
                    )
                }.map { "User removed successfully".toResponse() }
            },
            ResponseDto::class.java
    )
}