package com.withaion.backend.services

import com.withaion.backend.models.Role
import com.withaion.backend.models.User
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class KeycloakService(
        @Value("\${keycloak.admin.realm}") private val realmName: String,
        @Value("\${keycloak.admin.username}") private val username: String,
        @Value("\${keycloak.admin.password}") private val password: String
) {

    private final val keycloak = Keycloak.getInstance(
            "http://localhost:8080/auth",
            realmName,
            username,
            password,
            "admin-cli")
    private val realm = keycloak.realm(realmName)

    fun getUser(id: String): Mono<User> {
        return Mono.fromCallable { realm.users().get(id) }.map {
            User(it.toRepresentation(), it.roles().realmLevel().listEffective())
        }
    }

    fun getUsers(): Flux<User> {
        return Flux.fromIterable(realm.users().list())
                .filter { !(it.attributes != null && it.attributes.containsKey("isHidden")) }
                .map {
                    User(it, realm.users().get(it.id).roles().realmLevel().listEffective())
                }
    }

    fun createUser(user: Mono<User>): Mono<Int> {
        return user.map {
            val response = realm.users().create(it.toUserRepresentation())
            if (response.status in 200..299 && !it.password.isNullOrBlank()) {
                val newUserId = realm.users().search(it.username).firstOrNull()?.id
                val newCredentials = CredentialRepresentation()
                newCredentials.isTemporary = false
                newCredentials.type = CredentialRepresentation.PASSWORD
                newCredentials.value = it.password
                realm.users().get(newUserId).resetPassword(newCredentials)
            }
            response
        }.map { it.status }
                .doOnError { println(it.message) }
    }

    fun updateUser(id: String, user: Mono<User>): Mono<Int> {
        return user.map {
            realm.users().get(id).update(it.toUserRepresentation())
            200
        }
    }

    fun deleteUser(id: String): Mono<Int> {
        return Mono.just(id).map {
            realm.users().delete(it)
        }.map { it.status }
    }

    fun getRoles(): Flux<Role> {
        return Flux.fromIterable(realm.roles().list())
                .filter {
                    // Keycloak doesn't populate attributes of the role
                    // Therefore filtering by hardcoding the role names
                    it.name != "admin" && it.name != "create-realm"
                }
                .map { Role(it) }
    }

    fun setEnable(userId: String, isEnable: Boolean): Mono<Unit> {
        return Mono.fromCallable { realm.users().get(userId) }
                .map {
                    val representation = it.toRepresentation()
                    representation.isEnabled = isEnable
                    it.update(representation)
                }
    }

    fun addRole(userId: String, roleName: String): Mono<Unit> {
        return Mono.fromCallable { realm.roles().get(roleName).toRepresentation() }.map {
            realm.users().get(userId).roles().realmLevel().add(listOf(it))
        }
    }

    fun removeRole(userId: String, roleName: String): Mono<Unit> {
        return Mono.fromCallable { realm.roles().get(roleName).toRepresentation() }.map {
            realm.users().get(userId).roles().realmLevel().remove(listOf(it))
        }
    }
}