package com.withaion.backend.dto

import org.keycloak.representations.idm.UserRepresentation

/**
 * DTO for user creation requests
 *
 * @property username - Username of the new user
 * @property firstName - First name of the new user
 * @property lastName - Last name of the new user
 * @property email - Email of the new user
 * @property password - Password of the new user
 * */
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