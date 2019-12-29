package com.withaion.backend.dto

/**
 * DTO for requests to assign event user assignments
 *
 * @property email - Email of the user
 * @property role -  Role to be assigned
 * */
data class AssignUserDto(
        val email: String,
        val role: String
)