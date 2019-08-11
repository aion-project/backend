package com.withaion.backend.models

import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation

data class Role(
        val id: String?,
        val name: String?,
        val description: String?
) {
    constructor(role: RoleRepresentation) : this(
            role.id,
            role.name,
            role.description
    )
}