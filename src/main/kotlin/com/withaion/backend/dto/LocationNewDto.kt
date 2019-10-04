package com.withaion.backend.dto

import com.withaion.backend.models.Location

/**
 * DTO for location creation requests
 *
 * @property name - Name of the new location
 * @property level - Level of the new location
 * @property description - Description of the new location
 * */
data class LocationNewDto(
        val name: String,
        val level: String,
        val description: String?
) {

    fun toLocation(): Location {
        return Location(name = name, level = level, description = description)
    }

}