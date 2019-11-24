package com.withaion.backend.handlers

import com.withaion.backend.data.GroupRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.GroupChangeUserDto
import com.withaion.backend.dto.GroupNewDto
import com.withaion.backend.dto.GroupUpdateDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Group
import com.withaion.backend.models.GroupRef
import com.withaion.backend.models.UserRef
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class GroupHandler(
        private val groupRepository: GroupRepository,
        private val userRepository: UserRepository
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
                    val users: ArrayList<UserRef> = ArrayList(it.t2.users)
                    users.add(UserRef(it.t1))
                    val groups: ArrayList<GroupRef> = ArrayList(it.t1.groups)
                    groups.add(GroupRef(it.t2))

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
                    val users: ArrayList<UserRef> = ArrayList(it.t2.users)
                    users.remove(UserRef(it.t1))
                    val groups: ArrayList<GroupRef> = ArrayList(it.t1.groups)
                    groups.remove(GroupRef(it.t2))

                    Mono.zip(
                            userRepository.save(it.t1.copy(groups = groups)),
                            groupRepository.save(it.t2.copy(users = users))
                    )
                }.map { "User removed successfully".toResponse() }
            },
            ResponseDto::class.java
    )
}