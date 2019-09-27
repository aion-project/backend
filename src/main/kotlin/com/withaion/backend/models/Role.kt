package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import org.keycloak.representations.idm.RoleRepresentation

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Role(
        val id: String,
        val name: String,
        val description: String?
) {
    constructor(role: RoleRepresentation) : this(
            role.id,
            role.name,
            role.description
    )
}