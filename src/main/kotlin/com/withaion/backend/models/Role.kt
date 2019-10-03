package com.withaion.backend.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.okta.sdk.resource.group.Group

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Role(
        val id: String,
        val name: String,
        val description: String?
) {
    constructor(group: Group) : this(group.id, group.profile.name, group.profile.description)
}