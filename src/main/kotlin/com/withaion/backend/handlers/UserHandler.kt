package com.withaion.backend.handlers

import com.withaion.backend.data.UserDataRepository
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.dto.RoleDto
import com.withaion.backend.dto.UserNewDto
import com.withaion.backend.dto.UserUpdateDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.models.UserData
import com.withaion.backend.services.KeycloakService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class UserHandler(
        private val keycloakService: KeycloakService,
        private val userDataRepository: UserDataRepository
) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap { principle ->
                        return@flatMap fetchUser(principle.name)
                    },
            User::class.java
    )

    fun updateMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap {
                        request.bodyToMono(UserUpdateDto::class.java).flatMap { user ->
                            Mono.zip(
                                    keycloakService.updateUser(request.pathVariable("id"), Mono.just(user)),
                                    userDataRepository.save(UserData(request.pathVariable("id"), true, user.bio))
                            )
                        }
                    }
                    .map { "User updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            fetchUser(request.pathVariable("id")),
            User::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            keycloakService.getUsers().flatMap { user ->
                return@flatMap Mono.zip(
                        userDataRepository.findById(user.id).defaultIfEmpty(UserData(user.id, false)),
                        keycloakService.getUserRoles(user.id)
                ).map {
                    User(user, it.t1, it.t2)
                }
            },
            User::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.accepted().body(
            keycloakService.createUser(request.bodyToMono(UserNewDto::class.java))
                    .map { "User created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.accepted().body(
            request.bodyToMono(UserUpdateDto::class.java).flatMap { user ->
                Mono.zip(
                        keycloakService.updateUser(request.pathVariable("id"), Mono.just(user)),
                        userDataRepository.save(UserData(request.pathVariable("id"), true, user.bio))
                )
            }.map { "User updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.accepted().body(
            keycloakService.deleteUser(request.pathVariable("id")).map { "User deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setEnable(request: ServerRequest) = ServerResponse.ok().body(
            keycloakService.setEnable(request.pathVariable("id"), request.pathVariable("isEnable").toBoolean())
                    .map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun activate(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap {
                userDataRepository.save(UserData(it.name, true, null))
            }.map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java

    )

    fun addRole(request: ServerRequest) = ServerResponse.accepted().body(
            request.bodyToMono(RoleDto::class.java)
                    .flatMap { keycloakService.addRole(request.pathVariable("id"), it.roleName) }
                    .map { "Role added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeRole(request: ServerRequest) = ServerResponse.accepted().body(
            request.bodyToMono(RoleDto::class.java)
                    .flatMap { keycloakService.removeRole(request.pathVariable("id"), it.roleName) }
                    .map { "Role removed successfully".toResponse() },
            ResponseDto::class.java
    )

    /*
    * Utility functions
    * */
    private fun fetchUser(userId: String): Mono<User> {
        return Mono.zip(
                keycloakService.getUser(userId),
                userDataRepository.findById(userId)
                        .defaultIfEmpty(UserData(userId))
                        .onErrorReturn(UserData(userId)),
                keycloakService.getUserRoles(userId)
        ).map {
            User(it.t1, it.t2, it.t3)
        }
    }
}
