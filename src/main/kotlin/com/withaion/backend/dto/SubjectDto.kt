package com.withaion.backend.dto

import com.withaion.backend.models.Subject

/**
 * DTO for subject creation requests
 *
 * @property name - Name of the new subject
 * @property description - Description of the new subject
 * */
data class SubjectNewDto(
        val name: String,
        val description: String?
) {
    fun toSubject(): Subject {
        return Subject(name = name, description = description)
    }
}

/**
 * DTO for subject update requests
 *
 * @property name - Name of the subject
 * @property description - Description of the subject
 * */
data class SubjectUpdateDto(
        val name: String?,
        val description: String?
) {
    fun toUpdatedSubject(subject: Subject): Subject {
        var currentSubject = subject
        name?.let {
            currentSubject = currentSubject.copy(name = name)
        }
        description?.let {
            currentSubject = currentSubject.copy(description = description)
        }

        return currentSubject
    }
}
