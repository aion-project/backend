package com.withaion.backend.dto

/**
 * DTO for requests to assign event user assignments
 *
 * @property email - Email of the user
 * */
data class AssignUserDto(
        val email: String
)