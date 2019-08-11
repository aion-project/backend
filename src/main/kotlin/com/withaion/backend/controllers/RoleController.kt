package com.withaion.backend.controllers

import com.withaion.backend.models.Role
import com.withaion.backend.models.User
import com.withaion.backend.services.KeycloakService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/roles")
class RoleController(private val keycloakService: KeycloakService) {

    @GetMapping
    fun getAll(): Flux<Role> {
        return keycloakService.getRoles()
    }

}