package com.withaion.backend.dto

import com.withaion.backend.models.Resource

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
