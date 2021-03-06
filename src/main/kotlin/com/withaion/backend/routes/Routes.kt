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
                val handler = UserHandler(ref(), ref(), ref(), ref(), ref())

                // Personal endpoints
                "/me".nest {
                    GET("/") { handler.getMe(it) }
                    POST("/activate") { handler.activate(it) }
                    POST("/uploadAvatar") { handler.uploadAvatar(it) }
                    POST("/changePassword") { handler.changePassword(it) }
                }

                // User endpoints
                GET("/available") { handler.available(it) }
                GET("/count") { handler.count() }
                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                GET("/{id}/events") { handler.events(it) }
                POST("/{id}/setEnable/{isEnable}") { handler.setEnable(it) }
                POST("/{id}/addRole") { handler.addRole(it) }
                POST("/{id}/removeRole") { handler.removeRole(it) }
                POST("/{id}/setLocation") { handler.setLocation(it) }
                POST("/{id}/removeLocation") { handler.removeLocation(it) }
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
            "/roles".nest {
                val handler = RoleHandler(ref())
                GET("") { handler.getAll() }
            }
            "/locations".nest {
                val handler = LocationHandler(ref(), ref(), ref(), ref())

                GET("/available") { handler.available(it) }
                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                GET("/{id}/events") { handler.events(it) }
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
            "/events".nest {
                val handler = EventHandler(ref(), ref(), ref(), ref(), ref())

                GET("/mine") { handler.getMine(it) }
                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/setSubject") { handler.setSubject(it) }
                POST("/{id}/removeSubject") { handler.removeSubject(it) }
                POST("/{id}/addGroup") { handler.addGroup(it) }
                POST("/{id}/removeGroup") { handler.removeGroup(it) }
            }
            "/schedule".nest {
                val handler = ScheduleHandler(ref(), ref(), ref(), ref(), ref())

                POST("/") { handler.create(it) }
                POST("/{id}/reschedule") { handler.reschedule(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/setLocation") { handler.setLocation(it) }
                POST("/{id}/removeLocation") { handler.removeLocation(it) }
                POST("/{id}/addUser") { handler.addUser(it) }
                POST("/{id}/removeUser") { handler.removeUser(it) }
            }
            "/subjects".nest {
                val handler = SubjectHandler(ref(), ref())

                GET("/{id}") { handler.get(it) }
                GET("/") { handler.getAll() }
                POST("/") { handler.create(it) }
                PUT("/{id}") { handler.update(it) }
                DELETE("/{id}") { handler.delete(it) }
            }
            "/reschedule".nest {
                val handler = RescheduleHandler(ref(), ref(), ref())

                GET("/mine") { handler.getMine(it) }
                GET("/pending") { handler.getPending() }
                GET("/reviewed") { handler.getReviewed() }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/accept") { handler.accept(it) }
                POST("/{id}/decline") { handler.decline(it) }
            }
            "/reservation".nest {
                val handler = ReservationHandler(ref(), ref(), ref(), ref(), ref())

                GET("/mine") { handler.getMine(it) }
                GET("/open") { handler.getOpen() }
                GET("/closed") { handler.getClosed() }
                POST("/") { handler.create(it) }
                DELETE("/{id}") { handler.delete(it) }
                POST("/{id}/accept") { handler.accept(it) }
                POST("/{id}/review") { handler.review(it) }
                POST("/{id}/decline") { handler.decline(it) }
            }
        }
    }
}
