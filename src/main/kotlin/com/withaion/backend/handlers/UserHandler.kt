package com.withaion.backend.handlers

import com.withaion.backend.data.UserDataRepository
import com.withaion.backend.dto.*
import com.withaion.backend.exceptions.FieldConflictException
import com.withaion.backend.exceptions.FieldRequiredException
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.models.UserData
import com.withaion.backend.services.ImageService
import com.withaion.backend.services.KeycloakService
import org.springframework.http.HttpStatus
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class UserHandler(
        private val keycloakService: KeycloakService,
        private val userDataRepository: UserDataRepository,
        private val imageService: ImageService
) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap { findUserById(it.name) },
            User::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            findUserById(request.pathVariable("id")),
            User::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            keycloakService.getUsers().flatMap { user ->
                keycloakService.getUserRoles(user.id)
                        .map { User(user, it) }
            },
            User::class.java
    )

    fun create(request: ServerRequest) = keycloakService.createUser(request.bodyToMono(UserNewDto::class.java))
            .flatMap {
                ServerResponse.ok().syncBody("User created successfully".toResponse())
            }.onErrorResume {
                when (it) {
                    is FieldRequiredException -> ServerResponse.badRequest().syncBody(it.message.toResponse())
                    is FieldConflictException -> ServerResponse.status(HttpStatus.CONFLICT).syncBody(it.message.toResponse())
                    else -> it.message?.let { msg -> ServerResponse.badRequest().syncBody(msg.toResponse()) }
                }
            }

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(UserUpdateDto::class.java).flatMap { user ->
                Mono.zip(
                        keycloakService.updateUser(request.pathVariable("id"), Mono.just(user)),
                        userDataRepository.findById(request.pathVariable("id"))
                                .defaultIfEmpty(UserData(UserData.EMPTY_ID))
                                .flatMap {
                                    val updatedUserData = if (it.userId != UserData.EMPTY_ID)
                                        it.copy(bio = user.bio)
                                    else
                                        UserData(request.pathVariable("id"), bio = user.bio)
                                    userDataRepository.save(updatedUserData)
                                }
                )
            }.map { "User updated successfully".toResponse() },
            ResponseDto::class.java
    )

    fun delete(request: ServerRequest) = ServerResponse.ok().body(
            userDataRepository.deleteById(request.pathVariable("id"))
                    .then(keycloakService.deleteUser(request.pathVariable("id")))
                    .map { "User deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setEnable(request: ServerRequest) = ServerResponse.ok().body(
            keycloakService.setEnable(
                    request.pathVariable("id"),
                    request.pathVariable("isEnable").toBoolean()
            ).map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun activate(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap { userDataRepository.save(UserData(it.name, true, null)) }
                    .map { "Enable state changed successfully".toResponse() },
            ResponseDto::class.java

    )

    fun addRole(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(RoleDto::class.java)
                    .flatMap { keycloakService.addRole(request.pathVariable("id"), it.roleName) }
                    .map { "Role added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeRole(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(RoleDto::class.java)
                    .flatMap { keycloakService.removeRole(request.pathVariable("id"), it.roleName) }
                    .map { "Role removed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun uploadAvatar(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap { principal ->
                request.bodyToMono(FileUploadDto::class.java)
                        .map {
                            val data = Base64Utils.decodeFromString(it.data)
                            return@map imageService.store("avatar", it.ext, data)
                        }.flatMap { filename ->
                            userDataRepository.findById(principal.name).flatMap { userData ->
                                userDataRepository.save(userData.copy(
                                        avatarUrl = "/blob/avatar/original/$filename",
                                        thumbnailUrl = "/blob/avatar/thumbnail/$filename"
                                ))
                            }
                        }.map { "Imaged uploaded successfully".toResponse() }
            },
            ResponseDto::class.java
    )

    // Utility Functions

    /**
     * Fetch user by merging data from multiple services for a given user id
     *
     * @param userId - User id of the requested user
     * @return Mono of requested user
     * */
    private fun findUserById(userId: String): Mono<User> {
        return Mono.zip(
                keycloakService.getUser(userId),
                findUserDataById(userId),
                keycloakService.getUserRoles(userId)
        ).map {
            User(it.t1, it.t2, it.t3)
        }
    }

    /**
     * Fetch user data from UserRepository for a given user id
     *
     * @param userId - User if of the requested user
     * @return Mono of requested user data
     * */
    private fun findUserDataById(userId: String): Mono<UserData> {
        return userDataRepository.findById(userId)
                .defaultIfEmpty(UserData(userId))
    }
}

