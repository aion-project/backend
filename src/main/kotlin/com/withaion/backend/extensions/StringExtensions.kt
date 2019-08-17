package com.withaion.backend.extensions

import com.withaion.backend.dto.ResponseDto

fun String.toResponse(isSuccess: Boolean = true): ResponseDto {
    return ResponseDto(isSuccess, this)
}