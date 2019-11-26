package com.withaion.backend.dto

import com.withaion.backend.models.Location

/**
 * DTO for location creation requests
 *
 * @property name - Name of the new location
 * @property level - Level of the new location
 * @property description - Description of the new location
 * @property ac - Availability of air condition of the new location
 * */
data class LocationNewDto(
        val name: String,
        val level: String,
        val description: String?,
        val ac: Boolean
) {
    fun toLocation(): Location {
        return Location(name = name, level = level, description = description, ac = ac)
    }
}

/**
 * DTO for location update requests
 *
 * @property name - Name of the location
 * @property level - Level of the location
 * @property description - Description of the location
 * @property ac - Availability of air condition of the location
 * */
data class LocationUpdateDto(
        val name: String?,
        val level: String?,
        val description: String?,
        val ac: Boolean?
) {
    fun toUpdatedLocation(location: Location): Location {
        var currentLocation = location
        name?.let {
            currentLocation = currentLocation.copy(name = name)
        }
        level?.let {
            currentLocation = currentLocation.copy(level = level)
        }
        description?.let {
            currentLocation = currentLocation.copy(description = description)
        }
        ac?.let {
            currentLocation = currentLocation.copy(ac = ac)
        }
        return currentLocation
    }
}

class LocationChangeResourceDto(
        val resource: String
)