package com.withaion.backend.dto

import org.keycloak.representations.idm.UserRepresentation

/**
 * DTO for user update requests
 *
 * @property firstName - Updated first name of the user
 * @property lastName - Updated last name of the user
 * @property email - Updated email of the user
 * @property bio - Updated bio of the user
 * */
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