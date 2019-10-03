package com.withaion.backend.services

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
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
        return Mono.fromCallable {
            val builder = UserBuilder.instance()
            builder.setFirstName(user.firstName)
            builder.setLastName(user.lastName)
            builder.setEmail(user.email)
            builder.setLogin(user.email)
            builder.setPassword(user.password.toCharArray())
            builder.addGroup(getUserRole().id)
            builder.buildAndCreate(client)
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

    fun setRole(email: String, groupId: String): Mono<Boolean> {
        return Mono.fromCallable { client.getUser(email).addToGroup(groupId) }.thenReturn(true)
    }

    fun removeRole(email: String, groupId: String): Mono<Boolean> {
        return Mono.fromCallable { client.getGroup(groupId).removeUser(email) }.thenReturn(true)
    }

    fun getRoles(email: String): Mono<List<Role>> {
        return Mono.fromCallable {
            client.getUser(email).listGroups()
                    .filterNot { it.profile.name == EVERYONE_GROUP }
                    .map { Role(it) }
        }
    }

    fun getRoles(): Flux<Role> {
        return Flux.fromIterable(client.listGroups()
                .filterNot { it.profile.name == EVERYONE_GROUP }
                .map { Role(it) })
    }

    private fun getUserRole(): Role {
        return Role(client.listGroups("user", "", "").first())
    }

    companion object {
        private const val EVERYONE_GROUP = "Everyone"
    }
}