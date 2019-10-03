package com.withaion.backend.routes

import com.withaion.backend.handlers.BlobHandler
import com.withaion.backend.handlers.RoleHandler
import com.withaion.backend.handlers.UserHandler
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.router

val routerBeans = beans {
    bean {
        router {
            "/blob".nest {
                val handler = BlobHandler(ref(), ref())
                GET("/avatar/{type}/{filename:.+}") { handler.getAvatar(it) }
                GET("/{category}/{filename:.+}") { handler.getBlob(it) }
            }
            "/users".nest {
                val handler = UserHandler(ref(), ref(), ref())

                // Personal endpoints
                "/me".nest {
                    GET("/") { handler.getMe(it) }
                    POST("/activate") { handler.activate(it) }
                    POST("/uploadAvatar") { handler.uploadAvatar(it) }
                    POST("/changePassword") { handler.changePassword(it) }
                }

                // User endpoints
                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/setEnable/{isEnable}") { handler.setEnable(it) }
                POST("/{id}/addRole") { handler.addRole(it) }
                POST("/{id}/removeRole") { handler.removeRole(it) }
            }
            "/roles".nest {
                val handler = RoleHandler(ref())
                GET("") { handler.getAll() }
            }
        }
    }
}