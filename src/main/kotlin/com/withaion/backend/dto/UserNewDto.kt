package com.withaion.backend.dto

import org.keycloak.representations.idm.UserRepresentation

data class UserNewDto(
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
) {
    fun toUserRepresentation(): UserRepresentation {
        val user = UserRepresentation()
        user.username = username
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
        return user
    }
}