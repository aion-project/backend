package com.withaion.backend.dto

data class FileUploadDto(
     val ext: String,
     val mime: String,
     val data: String
)