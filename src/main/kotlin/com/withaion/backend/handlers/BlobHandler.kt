package com.withaion.backend.handlers

import com.withaion.backend.services.StorageService
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


class BlobHandler(
        private val storageService: StorageService
) {

    fun getBlob(request: ServerRequest) = ServerResponse.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + request.pathVariable("filename") + "\"")
            .body(
                    Mono.fromCallable {
                        val category = request.pathVariable("category")
                        val filename = request.pathVariable("filename")
                        return@fromCallable storageService.readBlob("/$category/$filename")
                    },
                    Resource::class.java
            )

}
