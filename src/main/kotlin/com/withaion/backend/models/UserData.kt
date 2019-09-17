package com.withaion.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "userData")
data class UserData(
        @Id val userId: String,
        val enabled: Boolean = false,
        val bio: String? = null
) {
    companion object {
        const val EMPTY_ID = "empty_id"
    }
}