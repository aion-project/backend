package com.withaion.backend.handlers

import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.FileUploadDto
import com.withaion.backend.dto.ResponseDto
import com.withaion.backend.dto.UserNewDto
import com.withaion.backend.dto.UserUpdateDto
import com.withaion.backend.exceptions.FieldConflictException
import com.withaion.backend.exceptions.FieldRequiredException
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.services.ImageService
import com.withaion.backend.services.OktaService
import org.springframework.http.HttpStatus
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class UserHandler(
        private val oktaService: OktaService,
        private val userRepository: UserRepository,
        private val imageService: ImageService
) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap { userRepository.findByEmail(it.name) },
            User::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")),
            User::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            userRepository.findAll(),
            User::class.java
    )

    fun create(request: ServerRequest) = request.bodyToMono(UserNewDto::class.java)
            .flatMap {
                Mono.zip(
                        oktaService.createUser(it),
                        userRepository.save(it.toUser())
                )
            }.flatMap {
                ServerResponse.ok().syncBody("User created successfully".toResponse())
            }.onErrorResume {
                when (it) {
                    is FieldRequiredException -> ServerResponse.badRequest().syncBody(it.message.toResponse())
                    is FieldConflictException -> ServerResponse.status(HttpStatus.CONFLICT).syncBody(it.message.toResponse())
                    else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
                }
            }

    fun update(request: ServerRequest) = request.bodyToMono(UserUpdateDto::class.java)
            .flatMap { user ->
                userRepository.findById(request.pathVariable("id")).flatMap {
                    Mono.zip(
                            oktaService.updateUser(it.email, user),
                            userRepository.save(user.toUpdatedUser(it))
                    )
                }

            }.flatMap { ServerResponse.ok().syncBody("User updated successfully".toResponse()) }
            .onErrorResume {
                when (it) {
                    is FieldConflictException -> ServerResponse.status(HttpStatus.CONFLICT).syncBody(it.message.toResponse())
                    else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
                }
            }

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                Mono.zip(
                        oktaService.deleteUser(user.email).thenReturn(true),
                        userRepository.deleteById(user.id!!).thenReturn(true)
                )
            }.map { "User deleted successfully".toResponse() },
            ResponseDto::class.java
    )

//    fun setEnable(request: ServerRequest) = ServerResponse.ok().body(
//            keycloakService.setEnable(
//                    request.pathVariable("id"),
//                    request.pathVariable("isEnable").toBoolean()
//            ).map { "Enable state changed successfully".toResponse() },userDataRepository
//            ResponseDto::class.java
//    )
//
//    fun activate(request: ServerRequest) = ServerResponse.ok().body(
//            request.principal()
//                    .flatMap { userDataRepository.save(UserData(it.name, true, null)) }
//                    .map { "Enable state changed successfully".toResponse() },
//            ResponseDto::class.java
//
//    )
//
//    fun addRole(request: ServerRequest) = ServerResponse.ok().body(
//            request.bodyToMono(RoleDto::class.java)
//                    .flatMap { keycloakService.addRole(request.pathVariable("id"), it.roleName) }
//                    .map { "Role added successfully".toResponse() },
//            ResponseDto::class.java
//    )
//
//    fun removeRole(request: ServerRequest) = ServerResponse.ok().body(
//            request.bodyToMono(RoleDto::class.java)
//                    .flatMap { keycloakService.removeRole(request.pathVariable("id"), it.roleName) }
//                    .map { "Role removed successfully".toResponse() },
//            ResponseDto::class.java
//    )

    fun uploadAvatar(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap { principal ->
                request.bodyToMono(FileUploadDto::class.java)
                        .map {
                            val data = Base64Utils.decodeFromString(it.data)
                            return@map imageService.store("avatar", it.ext, data)
                        }.flatMap { filename ->
                            userRepository.findByEmail(principal.name).flatMap { userData ->
                                userRepository.save(userData.copy(
                                        avatarUrl = "/blob/avatar/original/$filename",
                                        thumbnailUrl = "/blob/avatar/thumbnail/$filename"
                                ))
                            }
                        }.map { "Imaged uploaded successfully".toResponse() }
            },
            ResponseDto::class.java
    )
}

