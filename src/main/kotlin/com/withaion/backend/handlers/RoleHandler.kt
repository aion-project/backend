package com.withaion.backend.handlers

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class RoleHandler() {

    fun getAll() = ServerResponse.ok().syncBody(Mono.just("Not implemented"))

}