package com.withaion.backend.dto

import com.withaion.backend.models.Subject
import java.awt.Color
import java.lang.Integer.max
import java.util.*


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
        return Subject(name = name, description = description, color = getRandomColor())
    }

    private fun getRandomColor(): String {
        val rand = Random()

        val redValue = max(150, rand.nextInt(255))
        val greenValue = max(150, rand.nextInt(255))
        val blueValue = max(150, rand.nextInt(255))

        val color = Color(redValue, greenValue, blueValue)
        return "#"+Integer.toHexString(color.rgb).substring(2)
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
