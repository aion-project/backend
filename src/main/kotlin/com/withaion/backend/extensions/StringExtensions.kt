package com.withaion.backend.extensions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun String.toResponse(status: HttpStatus = HttpStatus.OK): ResponseEntity<String> {
    return ResponseEntity(this, status)
}