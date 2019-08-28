package com.withaion.backend.extensions

import com.withaion.backend.dto.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun String.toResponse(status: HttpStatus = HttpStatus.OK): ResponseEntity<ResponseDto> {
    return ResponseEntity(ResponseDto(this), status)
}