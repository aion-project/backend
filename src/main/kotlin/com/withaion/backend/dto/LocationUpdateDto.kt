package com.withaion.backend.dto

import com.withaion.backend.models.Location

/**
 * DTO for location update requests
 *
 * @property name - Name of the new location
 * @property level - Level of the new location
 * @property description - Description of the new location
 * */
data class LocationUpdateDto(
        val name: String?,
        val level: String?,
        val description: String?
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
        return currentLocation
    }

}