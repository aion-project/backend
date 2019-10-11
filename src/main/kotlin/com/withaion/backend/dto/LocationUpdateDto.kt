package com.withaion.backend.dto

import com.withaion.backend.models.Location

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