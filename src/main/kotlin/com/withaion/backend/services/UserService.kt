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
        return Mono.fromCallable { realm.users().get(id) }.map {
            User(it.toRepresentation(), it.roles().realmLevel().listEffective())
        }
    }

    fun getAll(): Flux<User> {
        return Flux.fromIterable(realm.users().list()).map {
            User(it, realm.users().get(it.id).roles().realmLevel().listEffective())
        }
    }

    fun create(user: Mono<User>): Mono<Int> {
        return user.map {
            realm.users().create(it.toUserRepresentation())
        }.map { it.status }
                .doOnError { println(it.message) }
    }

    fun update(id: String, user: Mono<User>): Mono<Int> {
        return user.map {
            realm.users().get(id).update(it.toUserRepresentation())
            200
        }
    }

    fun delete(id: String): Mono<Int> {
        return Mono.just(id).map {
            realm.users().delete(it)
        }.map { it.status }
    }
}