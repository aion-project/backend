package com.withaion.backend.handlers

import com.withaion.backend.models.Role
import com.withaion.backend.services.OktaService
import org.springframework.web.reactive.function.server.ServerResponse

class RoleHandler(
        private val oktaService: OktaService
) {

    fun getAll() = ServerResponse.ok().body(oktaService.getRoles(), Role::class.java)

}