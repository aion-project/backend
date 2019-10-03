package com.withaion.backend.dto

/**
 * DTO for user update requests
 *
 * @property currentPassword - Current password of the user
 * @property newPassword - New password of the user
 * */
data class ChangePasswordDto(
        val currentPassword: String,
        val newPassword: String
)