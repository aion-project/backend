package com.withaion.backend.dto

import com.withaion.backend.models.Group

class GroupNewDto(
        val name: String,
        val description: String
) {
    fun toGroup(): Group {
        return Group(name = name, description = description)
    }
}

class GroupUpdateDto(
        val name: String?,
        val description: String?
) {
    fun toUpdatedGroup(group: Group): Group {
        var currentGroup = group
        name?.let {
            currentGroup = currentGroup.copy(name = name)
        }
        description?.let {
            currentGroup = currentGroup.copy(description = description)
        }
        return currentGroup
    }
}

class GroupChangeUserDto(
        val user: String
)



