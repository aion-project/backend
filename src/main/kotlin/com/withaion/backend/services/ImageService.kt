package com.withaion.backend.services

import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.imageio.ImageIO

@Service
class ImageService(
        private val storageService: StorageService,
        @Value("\${storage.thumbnail.width}") private val thumbnailWidth: Int,
        @Value("\${storage.thumbnail.height}") private val thumbnailHeight: Int
) {

    @Throws(ImageResizeException::class)
    fun resolve(path: String, filename: String, type: String): Resource {
        return if (!type.endsWith(ORIGINAL_TYPE)) {

            if (storageService.existsBlob(path + "/" + getResizedFileName(filename, type))) {
                // Get image from storage if it is already resized.
                storageService.readBlob(path + "/" + getResizedFileName(filename, type))
            } else {
                // Resize image, store it and return it.
                resizeAndSave(path, filename, type)
            }
        } else {
            try {
                // Return original.
                storageService.readBlob("$path/$filename")
            } catch (exception: FileNotFoundException) {
                throw ImageResizeException(HttpStatus.NOT_FOUND, "The original image could not be found.")
            }
        }
    }

    @Throws(ImageResizeException::class)
    private fun resizeAndSave(path: String, filename: String, type: String): Resource {
        try {
            val resource = storageService.readBlob("$path/$filename")
            val resizedImage = getResizedImage(resource.file, type)
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(resizedImage, "jpg", outputStream)
            val outputByteArray = outputStream.toByteArray()
            storageService.writeBlob(path + "/" + getResizedFileName(filename, type), "image/jpeg", outputByteArray)
            return storageService.readBlob(path + "/" + getResizedFileName(filename, type))
        } catch (exception: FileNotFoundException) {
            throw ImageResizeException(HttpStatus.NOT_FOUND, "The original image could not be found.")
        } catch (exception: IOException) {
            throw ImageResizeException(HttpStatus.INTERNAL_SERVER_ERROR, "The resized image could not be saved.")
        }

    }

    @Throws(ImageResizeException::class)
    private fun getResizedImage(file: File, type: String): BufferedImage {
        try {
            val bufferedImage = ImageIO.read(file)
            return resize(bufferedImage, type)
        } catch (exception: IOException) {
            throw ImageResizeException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read the original image.")
        }

    }

    private fun getResizedFileName(reference: String, type: String): String {
        return type + "_" + reference
    }

    @Throws(ImageResizeException::class)
    private fun resize(bufferedImage: BufferedImage, type: String): BufferedImage {
        if (type != THUMBNAIL_TYPE) throw ImageResizeException(HttpStatus.BAD_REQUEST, "Requested type not found: $type")
        try {
            return Thumbnails.of(bufferedImage)
                    .height(thumbnailHeight)
                    .width(thumbnailWidth)
                    .asBufferedImage()
        } catch (exception: IOException) {
            throw ImageResizeException(HttpStatus.INTERNAL_SERVER_ERROR, "Image could not be resized to type: $type")
        }

    }

    class ImageResizeException(statusCode: HttpStatus, message: String) : Exception(statusCode.toString() + message)

    companion object {
        private const val ORIGINAL_TYPE = "original"
        private const val THUMBNAIL_TYPE = "thumbnail"
    }

}
