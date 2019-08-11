package com.withaion.backend.services

import com.withaion.backend.models.User
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.ws.rs.core.Response

@Service
class UserService {

    private final val keycloak = Keycloak.getInstance(
            "http://localhost:8080/auth",
            "master",
            "admin",
            "admin",
            "admin-cli")
    private val realm = keycloak.realm("master")

    fun get(id: String): Mono<User> {
        return Mono.fromCallable { realm.users().get(id) }.map { it.toRepresentation() }.map { User(it) }
    }

    fun getAll(): Flux<User> {
        return Flux.fromIterable(realm.users().list()).map { User(it) }
    }

    fun create(user: Mono<User>): Mono<Int> {
        return user.map {
            realm.users().create(it.toUserRepresentation())
        }.map { it.status }
    }

    fun update(user: Mono<User>): Mono<Int> {
        return user.map {
            realm.users().get(it.id).update(it.toUserRepresentation())
            200
        }
    }

    fun delete(id: Mono<String>): Mono<Int> {
        return id.map {
            realm.users().delete(it)
        }.map { it.status }
    }
}