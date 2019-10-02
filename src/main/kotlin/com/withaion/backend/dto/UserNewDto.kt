package com.withaion.backend.dto

import com.withaion.backend.models.User

/**
 * DTO for user creation requests
 *
 * @property firstName - First name of the new user
 * @property lastName - Last name of the new user
 * @property email - Email of the new user
 * @property password - Password of the new user
 * */
data class UserNewDto(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
) {

    fun toUser(): User {
        return User(firstName = firstName, lastName = lastName, email = email)
    }

}