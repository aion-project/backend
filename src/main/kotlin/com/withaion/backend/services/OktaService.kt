package com.withaion.backend.services

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.ChangePasswordRequest
import com.okta.sdk.resource.user.PasswordCredential

import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import com.withaion.backend.dto.ChangePasswordDto
import com.withaion.backend.dto.UserNewDto
import com.withaion.backend.dto.UserUpdateDto
import com.withaion.backend.models.Role
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OktaService(
        private val client: Client
) {

    fun createUser(user: UserNewDto): Mono<User> {
        return getUserRole().map {
            val builder = UserBuilder.instance()
            builder.setFirstName(user.firstName)
            builder.setLastName(user.lastName)
            builder.setEmail(user.email)
            builder.setLogin(user.email)
            builder.setPassword(user.password.toCharArray())
            builder.addGroup(it.id)
            builder.buildAndCreate(client)
        }.doOnError {
            println(it)
            println(it.message)
        }
    }

    fun updateUser(email: String, user: UserUpdateDto): Mono<User> {
        return Mono.fromCallable { client.getUser(email) }
                .map { currentUser ->
                    user.firstName?.let {
                        currentUser.profile.firstName = it
                    }
                    user.lastName?.let {
                        currentUser.profile.lastName = it
                    }
                    user.email?.let {
                        currentUser.profile.email = it
                        currentUser.profile.login = it
                    }
                    currentUser.update()
                }
    }

    fun deleteUser(email: String): Mono<Boolean> {
        return Mono.fromCallable {
            val user = client.getUser(email)
            user.deactivate()
            user.delete()
        }.thenReturn(true)
    }

    fun setEnable(email: String, isEnable: Boolean): Mono<Boolean> {
        return Mono.just(if (!isEnable) {
            client.getUser(email).suspend()
        } else {
            client.getUser(email).unsuspend()
        }).thenReturn(true)
    }

    fun changePassword(email: String, changePasswordDto: ChangePasswordDto): Mono<Boolean> {
        return Mono.fromCallable {
            client.getUser(email).changePassword(client.instantiate(ChangePasswordRequest::class.java)
                    .setOldPassword(client.instantiate(PasswordCredential::class.java).setValue(changePasswordDto.currentPassword.toCharArray()))
                    .setNewPassword(client.instantiate(PasswordCredential::class.java).setValue(changePasswordDto.newPassword.toCharArray())))
        }.map { true }
    }

    fun setRole(email: String, groupId: String): Mono<Boolean> {
        return Mono.fromCallable { client.getUser(email).addToGroup(groupId) }.thenReturn(true)
    }

    fun removeRole(email: String, groupId: String): Mono<Boolean> {
        return Mono.fromCallable {
            val user = client.getUser(email)
            client.getGroup(groupId).removeUser(user.id)
        }.thenReturn(true)
    }

    fun getRoles(): Flux<Role> {
        return Flux.fromIterable(client.listGroups()
                .filterNot { it.profile.name == EVERYONE_GROUP }
                .map { Role(it) })
    }

    fun getRole(roleId: String): Mono<Role> {
        return Mono.fromCallable { client.getGroup(roleId) }
                .map { Role(it) }
    }

    fun getUserRole(): Mono<Role> {
        return Mono.fromCallable { Role(client.listGroups("user", "", "").first()) }
    }

    companion object {
        private const val EVERYONE_GROUP = "Everyone"
    }
}