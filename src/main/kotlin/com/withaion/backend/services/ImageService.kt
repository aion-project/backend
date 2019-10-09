package com.withaion.backend.services

import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageService(
        private val storageService: StorageService,
        @Value("\${storage.thumbnail.width}") private val thumbnailWidth: Int,
        @Value("\${storage.thumbnail.height}") private val thumbnailHeight: Int
) {

    /**
     * Store a image using given data in the given path with randomly generated file with given extension
     *
     * @param path - Path of the directory of the resource
     * @param ext - Extension of the resource
     * @param data - Image data to be stored
     * @return Generated random file name string
     * */
    fun store(path: String, ext: String, data: ByteArray): String {
        val filename = "${UUID.randomUUID()}.$ext"
        storageService.writeBlob("$path/$filename", "image/jpeg", data)
        return filename;
    }

    /**
     * Retrieve image for the given filename of the given type in the given path
     *
     * @param path - Path of the directory of the resource
     * @param filename - Filename of the resource to be retrieved
     * @param type - Type of the resource to be retrieved
     * @return Resource of the requested image
     * */
    @Throws(ImageResizeException::class)
    fun resolve(path: String, filename: String, type: String): Resource {
        return if (!type.endsWith(ORIGINAL_TYPE)) {
            if (storageService.existsBlob(path + "/" + getResizedFileName(filename, type))) {
                storageService.readBlob(path + "/" + getResizedFileName(filename, type))
            } else {
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
            val resizedImage = getResizedImage(resource.inputStream, type)
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
    private fun getResizedImage(inputStream: InputStream, type: String): BufferedImage {
        try {
            val bufferedImage = ImageIO.read(inputStream)
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
