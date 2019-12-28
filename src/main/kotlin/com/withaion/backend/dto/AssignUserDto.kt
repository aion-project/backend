package com.withaion.backend.dto

/**
 * DTO for requests to assign and remove event user assignments
 *
 * @property userId - Id of the user
 * @property roleId - Id of the role
 * */
data class AssignUserDto(
        val userId: String,
        val roleId: String
)