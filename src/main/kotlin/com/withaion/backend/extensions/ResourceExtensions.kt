package com.withaion.backend.extensions

import com.okta.sdk.resource.ResourceException
import com.withaion.backend.dto.ResponseDto

fun String.toResponse(): ResponseDto {
    return ResponseDto(this)
}

fun ResourceException.toResponse(): ResponseDto {
    return this.error.causes.first().summary.toResponse()
}

