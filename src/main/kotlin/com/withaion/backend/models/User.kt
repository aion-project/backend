package com.withaion.backend.models

import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation

data class User(
        val id: String?,
        val username: String?,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val roles: List<String>?,
        val password: String? = null
) {
    constructor(user: UserRepresentation, roles: List<RoleRepresentation>? = null) : this(
            user.id,
            user.username,
            user.firstName,
            user.lastName,
            user.email,
            roles?.map { it.name }
    )

    fun toUserRepresentation(): UserRepresentation {
        val user = UserRepresentation()
        user.username = username
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
        user.isEnabled = true
        return user
    }
}