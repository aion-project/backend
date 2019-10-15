package com.withaion.backend.dto

import com.withaion.backend.models.Location
import com.withaion.backend.models.Resource

/**
 * DTO for resource creation requests
 *
 * @property name - Name of the new resource
 * @property description - Description of the new resource
 * */
data class ResourceNewDto(
        val name: String,
        val description: String?
) {

    fun toResource(): Resource {
        return Resource(name = name, description = description)
    }

}
