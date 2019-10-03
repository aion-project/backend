package com.withaion.backend.handlers

import com.okta.sdk.resource.ResourceException
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.services.ImageService
import com.withaion.backend.services.OktaService
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
            request.principal().flatMap { principal ->
                userRepository.findByEmail(principal.name).flatMap { user ->
                    oktaService.getRoles(user.email).map {
                        user.copy(roles = it)
                    }
                }
            },
            User::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                oktaService.getRoles(user.email).map {
                    user.copy(roles = it)
                }
            },
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
                    is ResourceException -> ServerResponse.status(it.error.status).syncBody(it.toResponse())
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
                    is ResourceException -> ServerResponse.status(it.error.status).syncBody(it.toResponse())
                    else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
                }
            }

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                Mono.zip(
                        oktaService.deleteUser(user.email),
                        userRepository.deleteById(user.id!!).thenReturn(true)
                )
            }.map { "User deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setEnable(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                val isEnable = request.pathVariable("isEnable").toBoolean()
                Mono.zip(
                        oktaService.setEnable(user.email, isEnable),
                        userRepository.save(user.copy(enabled = isEnable))
                )
            }.map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun activate(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap {
                userRepository.findByEmail(it.name).flatMap { user ->
                    userRepository.save(user.copy(active = true))
                }
            }.map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun addRole(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                request.bodyToMono(RoleDto::class.java)
                        .flatMap { oktaService.setRole(user.email, it.roleId) }
            }.map { "Role added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeRole(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                request.bodyToMono(RoleDto::class.java)
                        .flatMap { oktaService.removeRole(user.email, it.roleId) }
            }.map { "Role removed successfully".toResponse() },
            ResponseDto::class.java
    )

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

    fun changePassword(request: ServerRequest) = request.principal()
            .flatMap { principal ->
                request.bodyToMono(ChangePasswordDto::class.java).flatMap { oktaService.changePassword(principal.name, it) }
            }.flatMap { ServerResponse.ok().syncBody("Password updated successfully".toResponse()) }
            .onErrorResume {
                when (it) {
                    is ResourceException -> ServerResponse.status(it.error.status).syncBody(it.toResponse())
                    else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
                }
            }

}

