package com.withaion.backend.dto

import com.withaion.backend.models.User

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
    fun toUpdatedUser(user: User): User {
        var currentUser = user
        firstName?.let {
            currentUser = currentUser.copy(firstName = firstName)
        }
        lastName?.let {
            currentUser = currentUser.copy(lastName = lastName)
        }
        email?.let {
            currentUser = currentUser.copy(email = email)
        }
        bio?.let {
            currentUser = currentUser.copy(bio = bio)
        }
        return currentUser;
    }
}