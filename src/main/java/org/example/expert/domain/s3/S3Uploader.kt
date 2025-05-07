package org.example.expert.domain.s3

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.io.IOException
import java.util.*

@Component
class S3Uploader (
    private val s3Client: S3Client)
{

    companion object {
        private val ALLOWED_TYPES = listOf("image/jpeg", "image/png", "image/gif", "image/webp")
        private val ALLOWED_EXTENSIONS = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp")
    }

    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucketName: String

    fun uploadFile(file: MultipartFile, dir: String): String {
        val contentType = file.contentType
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw IllegalArgumentException("허용되지 않는 이미지 형식입니다.")
        }
        val extension = extractExtension(file)
        val key = dir + "/" + UUID.randomUUID() + extension

        try {
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build()

            s3Client?.putObject(putObjectRequest, RequestBody.fromInputStream(file.inputStream, file.size))
            return key
        } catch (e: IOException) {
            throw RuntimeException("파일 업로드 실패", e)
        }
    }

    fun deleteProfileImage(userId: Long, key: String?) {
        if (key.isNullOrBlank()) return

        try {
            val request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

            s3Client.deleteObject(request)
        } catch (e: S3Exception) {
            println("S3 삭제 실패: ${e.awsErrorDetails().errorMessage()}")
        }
    }


    private fun extractExtension(file: MultipartFile): String {
        val originalFilename = file.originalFilename
        if (originalFilename.isNullOrBlank() || !originalFilename.contains(".")) {
            throw IllegalArgumentException("파일 이름이 유효하지 않습니다.")
        }

        val extension = originalFilename.substringAfterLast(".", "").let { ".$it".lowercase() }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw IllegalArgumentException("이미지 확장자만 업로드할 수 있습니다.")
        }

        return extension
    }

    fun getFileUrl(key: String): String {
        return s3Client.utilities()
            .getUrl { it.bucket(bucketName).key(key) }
            .toExternalForm()
    }


}
