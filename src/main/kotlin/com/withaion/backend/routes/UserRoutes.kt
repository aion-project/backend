package com.withaion.backend.routes

import com.withaion.backend.models.User
import com.withaion.backend.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.PUT
import org.springframework.web.reactive.function.server.RequestPredicates.DELETE
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Configuration
class UserRoutes(private val userService: UserService) {

    private final val userRoute = "users"
    private final val meRoute = "me"

    @Bean
    fun getMe(): RouterFunction<ServerResponse> {
        return route(GET("/$meRoute"), HandlerFunction<ServerResponse> {
            ok().body(it.principal().flatMap { principal -> userService.get(principal.name) }, User::class.java)
        })
    }

    @Bean
    fun updateMe(): RouterFunction<ServerResponse> {
        return route(PUT("/$meRoute"), HandlerFunction {
            ok().body(it.principal().flatMap { principal ->
                userService.update(principal.name, it.bodyToMono(User::class.java))
            }, Int::class.java)
        })
    }

    @Bean
    fun get(): RouterFunction<ServerResponse> {
        return route(GET("/$userRoute/{id}"), HandlerFunction<ServerResponse> {
            ok().body(userService.get(it.pathVariable("id")), User::class.java)
        })
    }

    @Bean
    fun getAll(): RouterFunction<ServerResponse> {
        return route(GET("/$userRoute"), HandlerFunction<ServerResponse> {
            ok().body(userService.getAll(), User::class.java)
        })
    }

    @Bean
    fun create(): RouterFunction<ServerResponse> {
        return route(POST("/$userRoute"), HandlerFunction {
            ok().body(userService.create(it.bodyToMono(User::class.java)), Int::class.java)
        })
    }

    @Bean
    fun update(): RouterFunction<ServerResponse> {
        return route(PUT("/$userRoute/{id}"), HandlerFunction {
            ok().body(userService.update(it.pathVariable("id"), it.bodyToMono(User::class.java)), Int::class.java)
        })
    }

    @Bean
    fun delete(): RouterFunction<ServerResponse> {
        return route(DELETE("/$userRoute/{id}"), HandlerFunction {
            ok().body(userService.delete(it.pathVariable("id")), Int::class.java)
        })
    }

}