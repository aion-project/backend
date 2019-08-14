package com.withaion.backend.extensions

import com.withaion.backend.dto.Response

fun String.toResponse(isSuccess: Boolean = true): Response {
    return Response(isSuccess, this)
}