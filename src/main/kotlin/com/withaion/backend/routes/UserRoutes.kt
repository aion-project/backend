package com.withaion.backend.routes

import com.withaion.backend.models.User
import com.withaion.backend.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Configuration
class UserRoutes(private val userService: UserService) {

    private final val baseRoute = "users"

    @Bean
    fun get(): RouterFunction<ServerResponse> {
        return route(GET("/$baseRoute/me"), HandlerFunction<ServerResponse> {
            ok().body(it.principal().flatMap { principal -> userService.getUser(principal.name) }, User::class.java)
        })
    }

}