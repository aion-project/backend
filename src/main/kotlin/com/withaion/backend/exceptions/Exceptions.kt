package com.withaion.backend.exceptions

class FieldRequiredException(override val message: String = "Fields are missing") : Exception()
class FieldConflictException(override val message: String = "Conflict in provided fields") : Exception()