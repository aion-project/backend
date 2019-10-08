package com.withaion.backend.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gcp.storage.GoogleStorageResource
import org.springframework.core.io.Resource
import org.springframework.core.io.WritableResource
import org.springframework.stereotype.Service
import java.nio.file.NoSuchFileException

@Service
class StorageService(
        @Value("\${storage.protocol}://\${storage.bucket}/") private val storageResource: Resource
) {

    fun existsBlob(path: String): Boolean {
        return storageResource.createRelative(path).exists()
    }

    fun readBlob(path: String): Resource {
        return storageResource.createRelative(path)
    }

    @Throws(NoSuchFileException::class)
    fun writeBlob(path: String, contentType: String, data: ByteArray) {
        val writableResource = storageResource.createRelative(path) as WritableResource
        if (writableResource is GoogleStorageResource) {
            val blob = writableResource.createBlob()
            blob.toBuilder().setContentType(contentType).build().update()
        }
        writableResource.outputStream.use { os -> os.write(data) }
    }

}