package com.withaion.backend.controllers

import com.withaion.backend.dto.RoleDto
import com.withaion.backend.extensions.toResponse
import com.withaion.backend.models.User
import com.withaion.backend.services.KeycloakService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/users")
class UserController(private val keycloakService: KeycloakService) {

    @GetMapping("/me")
    fun getMe(principal: Mono<Principal>): Mono<User> {
        return principal.flatMap { keycloakService.getUser(it.name) }
    }

    @PutMapping("/me")
    fun updateMe(@RequestBody user: Mono<User>, principal: Mono<Principal>): Mono<Int> {
        return principal.flatMap {
            keycloakService.updateUser(it.name, user)
        }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: String): Mono<User> {
        return keycloakService.getUser(id)
    }

    @GetMapping
    fun getAll(): Flux<User> {
        return keycloakService.getUsers()
    }

    @PostMapping
    fun create(@RequestBody user: Mono<User>): Mono<ResponseEntity<String>> {
        return keycloakService.createUser(user).map { "User created successfully".toResponse(HttpStatus.CREATED) }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String, @RequestBody user: Mono<User>): Mono<ResponseEntity<String>> {
        return keycloakService.updateUser(id, user).map { "User updated successfully".toResponse(HttpStatus.CREATED) }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): Mono<ResponseEntity<String>> {
        return keycloakService.deleteUser(id).map { "User deleted successfully".toResponse(HttpStatus.ACCEPTED) }
    }

    @PostMapping("/{userId}/setEnable/{isEnable}")
    fun setEnable(@PathVariable("userId") userId: String, @PathVariable isEnable: Boolean): Mono<ResponseEntity<String>> {
        return keycloakService.setEnable(userId, isEnable).map { "Enable state changed successfully".toResponse(HttpStatus.ACCEPTED) }
    }

    @PostMapping("/{userId}/addRole")
    fun addRole(@PathVariable("userId") userId: String, @RequestBody role: Mono<RoleDto>): Mono<ResponseEntity<String>> {
        return role.flatMap {
            keycloakService.addRole(userId, it.roleName).map { "Role added successfully".toResponse(HttpStatus.CREATED) }
        }
    }

    @PostMapping("/{userId}/removeRole")
    fun removeRole(@PathVariable("userId") userId: String, @RequestBody role: Mono<RoleDto>): Mono<ResponseEntity<String>> {
        return role.flatMap {
            keycloakService.removeRole(userId, it.roleName).map { "Role removed successfully".toResponse(HttpStatus.ACCEPTED) }
        }
    }

}
