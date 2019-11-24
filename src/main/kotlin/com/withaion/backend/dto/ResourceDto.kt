package com.withaion.backend.dto

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

/**
 * DTO for resource update requests
 *
 * @property name - Name of the resource
 * @property description - Description of the resource
 * */
data class ResourceUpdateDto(
        val name: String?,
        val description: String?
) {
    fun toUpdatedLocation(resource: Resource): Resource {
        var currentResource = resource
        name?.let {
            currentResource = currentResource.copy(name = name)
        }
        description?.let {
            currentResource = currentResource.copy(description = description)
        }

        return currentResource
    }
}
