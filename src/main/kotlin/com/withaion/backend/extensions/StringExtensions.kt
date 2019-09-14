package com.withaion.backend.extensions

import com.withaion.backend.dto.ResponseDto

fun String.toResponse(): ResponseDto {
    return ResponseDto(this)
}