package com.withaion.backend.routes

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Configuration
class UserRoutes {


    @Bean
    fun get(): RouterFunction<ServerResponse> {
        return route(GET("/user"), HandlerFunction<ServerResponse> {
            ok().body(it.principal().map { it.name }, String::class.java)
        })
    }

}