package com.withaion.backend.dto

import org.keycloak.representations.idm.UserRepresentation

data class UserUpdateDto(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val bio: String?
) {
    fun toUserRepresentation(): UserRepresentation {
        val user = UserRepresentation()
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
        return user
    }
}