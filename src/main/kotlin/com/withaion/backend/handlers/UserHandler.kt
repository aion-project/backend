package com.withaion.backend.handlers

import com.withaion.backend.data.UserDataRepository
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.models.UserData
import com.withaion.backend.services.KeycloakService
import com.withaion.backend.services.StorageService
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class UserHandler(
        private val keycloakService: KeycloakService,
        private val userDataRepository: UserDataRepository,
        private val storageService: StorageService
) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal()
                    .flatMap { principle ->
                        return@flatMap findUserById(principle.name)
                    },
            User::class.java
    )

    fun get(request: ServerRequest) = ServerResponse.ok().body(
            findUserById(request.pathVariable("id")),
            User::class.java
    )

    fun getAll() = ServerResponse.ok().body(
            keycloakService.getUsers().flatMap { user ->
                return@flatMap Mono.zip(
                        findUserDataById(user.id),
                        keycloakService.getUserRoles(user.id)
                ).map {
                    User(user, it.t1, it.t2)
                }
            },
            User::class.java
    )

    fun create(request: ServerRequest) = ServerResponse.ok().body(
            keycloakService.createUser(request.bodyToMono(UserNewDto::class.java))
                    .map { "User created successfully".toResponse() },
            ResponseDto::class.java
    )

    fun update(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(UserUpdateDto::class.java).flatMap { user ->
                Mono.zip(
                        keycloakService.updateUser(request.pathVariable("id"), Mono.just(user)),
                        userDataRepository.findById(request.pathVariable("id")).defaultIfEmpty(UserData(UserData.EMPTY_ID)).flatMap {
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
            userDataRepository.deleteById(request.pathVariable("id")).then(
                    keycloakService.deleteUser(request.pathVariable("id"))
            ).map {
                "User deleted successfully".toResponse()
            },
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
                request.bodyToMono(FileUploadDto::class.java).map {
                    val filename = "${principal.name}.${it.ext}"
                    val data = Base64Utils.decodeFromString(it.data)
                    storageService.writeBlob("avatar/$filename", it.mime, data)
                    return@map filename
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
