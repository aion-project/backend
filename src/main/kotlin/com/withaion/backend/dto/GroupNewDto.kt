package com.withaion.backend.dto

import com.withaion.backend.models.Group

class GroupNewDto (
        val name : String,
        val description : String
) {
    fun toGroup(): Group {
        return Group(name = name , description = description )
    }
}
