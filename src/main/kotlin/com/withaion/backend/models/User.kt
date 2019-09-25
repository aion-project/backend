package com.withaion.backend.models

import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation

data class User(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val enabled: Boolean = true,
        val active: Boolean = true,
        val roles: List<String>?,
        val avatarUrl: String?,
        val thumbnailUrl: String?,
        val bio: String?
) {
    constructor(user: UserRepresentation, userData: UserData, roles: List<RoleRepresentation>? = null) : this(
            user.id,
            user.username,
            user.firstName,
            user.lastName,
            user.email,
            user.isEnabled,
            userData.enabled,
            roles?.map { it.name },
            userData.avatarUrl,
            userData.thumbnailUrl,
            userData.bio
    )
}