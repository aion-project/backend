package com.withaion.backend.handlers

import com.okta.sdk.resource.ResourceException
import com.withaion.backend.data.LocationRepository
import com.withaion.backend.data.UserRepository
import com.withaion.backend.dto.*
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.Event
import com.withaion.backend.models.Group
import com.withaion.backend.models.Location
import com.withaion.backend.models.User
import com.withaion.backend.services.ImageService
import com.withaion.backend.services.OktaService
import com.withaion.backend.utils.EventUtil
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class UserHandler(
        private val oktaService: OktaService,
        private val userRepository: UserRepository,
        private val locationRepository: LocationRepository,
        private val imageService: ImageService,
        private val mongoTemplate: ReactiveMongoTemplate
) {

    fun getMe(request: ServerRequest) = ServerResponse.ok().body(
            request.principal().flatMap { principal ->
                userRepository.findByEmail(principal.name)
            },
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

    fun search(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findAll(),
            User::class.java
    )

    fun available(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findAll().filterWhen { user ->
                val timeParam = request.queryParam("time")
                if (timeParam.isEmpty) return@filterWhen Mono.just(false)

                val time = LocalDateTime.parse(timeParam.get().substring(0, 19))

                val events = mutableListOf<Event>()
                user.groups.forEach {
                    events.addAll(it.events)
                }
                user.schedules.forEach {
                    it?.event?.let { it1 -> events.add(it1) }
                }

                Flux.fromIterable(events.flatMap {
                    EventUtil.expandEvents(it.schedules)
                }).any { event ->
                    (time.isAfter(event.startDateTime) || time.isEqual(event.startDateTime)) &&
                            (time.isBefore(event.endDateTime) || time.isEqual(event.endDateTime))
                }.map {
                    !it
                }
            },
            User::class.java
    )

    fun create(request: ServerRequest) = request.bodyToMono(UserNewDto::class.java)
            .flatMap {
                oktaService.getUserRole().flatMap { userRole ->
                    Mono.zip(
                            oktaService.createUser(it),
                            userRepository.save(it.toUser(userRole))
                    )
                }
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
                val update: Update = Update().pull("users", user)

                Mono.zip(
                        mongoTemplate.upsert(Query(), update, Group::class.java),
                        // TODO - User assignment deletion
                        oktaService.deleteUser(user.email),
                        userRepository.deleteById(user.id!!).thenReturn(true)
                )
            }.map { "User deleted successfully".toResponse() },
            ResponseDto::class.java
    )

    fun events(request: ServerRequest) = ServerResponse.ok().body(
            Flux.from(
                    userRepository.findById(request.pathVariable("id"))
            ).map { user ->
                val events = mutableListOf<Event>()
                user.groups.forEach {
                    events.addAll(it.events)
                }
                user.schedules.forEach {
                    it?.event?.let { it1 -> events.add(it1) }
                }
                events
            }.flatMap { events ->
                Flux.fromIterable(events.flatMap {
                    EventUtil.expandEvents(it.schedules)
                })
            },
            ScheduledEvent::class.java
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
            request.bodyToMono(RoleDto::class.java).flatMap { req ->
                Mono.zip(
                        oktaService.getRole(req.roleId),
                        userRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    val roles = it.t2.roles.toMutableSet()
                    roles.add(it.t1)

                    Mono.zip(
                            userRepository.save(it.t2.copy(roles = roles.toList())),
                            oktaService.setRole(it.t2.email, it.t1.id)
                    )
                }
            }.map { "Role added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeRole(request: ServerRequest) = ServerResponse.ok().body(
            request.bodyToMono(RoleDto::class.java).flatMap { req ->
                Mono.zip(
                        oktaService.getRole(req.roleId),
                        userRepository.findById(request.pathVariable("id"))
                ).flatMap {
                    val roles = it.t2.roles.toMutableSet()
                    roles.remove(it.t1)

                    Mono.zip(
                            userRepository.save(it.t2.copy(roles = roles.toList())),
                            oktaService.removeRole(it.t2.email, it.t1.id)
                    )
                }
            }.map { "Role removed successfully".toResponse() },
            ResponseDto::class.java
    )

    fun setLocation(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id")).flatMap { user ->
                request.bodyToMono(IdDto::class.java)
                        .flatMap { locationRepository.findById(it.id) }
                        .map { user.copy(location = it) }
                        .flatMap { userRepository.save(it) }
            }.map { "Location added successfully".toResponse() },
            ResponseDto::class.java
    )

    fun removeLocation(request: ServerRequest) = ServerResponse.ok().body(
            userRepository.findById(request.pathVariable("id"))
                    .map { user -> user.copy(location = null) }
                    .flatMap { userRepository.save(it) }
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

