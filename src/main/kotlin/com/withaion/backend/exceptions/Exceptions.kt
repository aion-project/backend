package com.withaion.backend.exceptions

class FieldRequiredException(override val message: String?) : Exception()
class FieldConflictException(override val message: String?) : Exception()