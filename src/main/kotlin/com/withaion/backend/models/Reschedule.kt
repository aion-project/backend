package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class RescheduleStatus {
    ACCEPTED, DECLINED, PENDING
}

enum class RescheduleType {
    TEMP, PERM
}

@Document(collection = "reschedule")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Reschedule(
        @Id val id: String? = null,
        @DBRef @JsonBackReference val event: Event? = null,
        val oldDateTime: LocalDateTime,
        val newDateTime: LocalDateTime,
        val status: RescheduleStatus = RescheduleStatus.PENDING,
        val type: RescheduleType = RescheduleType.TEMP,
        @DBRef val requestedBy: User? = null
)