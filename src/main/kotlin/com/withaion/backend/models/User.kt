package com.withaion.backend.models

import org.keycloak.representations.idm.UserRepresentation

data class User(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String
) {
    constructor(user: UserRepresentation) : this(user.id, user.username, user.firstName, user.lastName, user.email)

    fun toUserRepresentation(): UserRepresentation {
        val user = UserRepresentation()
        user.username = username
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
        return user
    }
}