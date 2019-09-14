package com.withaion.backend.handlers

import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.dto.RoleDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.services.KeycloakService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class UserHandler(private val keycloakService: KeycloakService) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap { keycloakService.getUser(it.name) },
            User::class.java
    )

    fun updateMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap { keycloakService.updateUser(it.name, request.bodyToMono(User::class.java)) }
                    .map { "User updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            keycloakService.getUser(request.pathVariable("id")),
            User::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            keycloakService.getUsers(),
            User::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.accepted().body(
            keycloakService.createUser(request.bodyToMono(User::class.java))
                    .map { "User created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.accepted().body(
            keycloakService.updateUser(request.pathVariable("id"), request.bodyToMono(User::class.java))
                    .map { "User updated successfully".toResponse() },
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

}
