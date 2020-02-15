package com.withaion.backend.exceptions

import com.withaion.backend.extensions.toResponse

class InvalidStateException : Exception() {
    val response = "Invalid state".toResponse()
}