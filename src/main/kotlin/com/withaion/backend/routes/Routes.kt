@file:Suppress("DuplicatedCode")

package com.withaion.backend.routes

import com.withaion.backend.handlers.*
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
                val handler = UserHandler(ref(), ref(), ref(), ref(), ref(), ref())

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
                GET("/search") { handler.search(it) }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/setEnable/{isEnable}") { handler.setEnable(it) }
                POST("/{id}/addRole") { handler.addRole(it) }
                POST("/{id}/removeRole") { handler.removeRole(it) }
                POST("/{id}/setLocation") { handler.setLocation(it) }
                POST("/{id}/removeLocation") { handler.removeLocation(it) }
            }
            "/roles".nest {
                val handler = RoleHandler(ref())
                GET("") { handler.getAll() }
            }
            "/locations".nest {
                val handler = LocationHandler(ref(), ref(), ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                GET("/{id}/getEvents") { handler.getEvents(it) }
                POST("/{id}/addResource") { handler.addResource(it) }
                POST("/{id}/removeResource") { handler.removeResource(it) }
            }
            "/resources".nest {
                val handler = ResourceHandler(ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
            }
            "/groups".nest {
                val handler = GroupHandler(ref(), ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/addUser") { handler.addUser(it) }
                POST("/{id}/removeUser") { handler.removeUser(it) }
            }
            "/events".nest {
                val handler = EventHandler(ref(), ref(), ref(), ref(), ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                GET("/{id}/getAssignments") { handler.getAssignments(it) }
                POST("/{id}/addAssignment") { handler.addAssignment(it) }
                POST("/{id}/removeAssignment") { handler.removeAssignment(it) }
                POST("/{id}/setSubject") { handler.setSubject(it) }
                POST("/{id}/removeSubject") { handler.removeSubject(it) }
                POST("/{id}/addGroup") { handler.addGroup(it) }
                POST("/{id}/removeGroup") { handler.removeGroup(it) }
                POST("/{id}/setLocation") { handler.setLocation(it) }
                POST("/{id}/removeLocation") { handler.removeLocation(it) }
            }
            "/subjects".nest {
                val handler = SubjectHandler(ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
            }
        }
    }
}
