package com.withaion.backend.handlers

import com.withaion.backend.models.Role
import com.withaion.backend.services.KeycloakService
import org.springframework.web.reactive.function.server.ServerResponse

class RoleHandler(private val keycloakService: KeycloakService) {

    fun getAll() = ServerResponse.ok().body(keycloakService.getRoles(), Role::class.java)

}