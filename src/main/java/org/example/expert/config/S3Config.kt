package org.example.expert.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.access-key}")
    private val accessKey: String,

    @Value("\${cloud.aws.credentials.secret-key}")
    private val secretKey: String,

    @Value("\${cloud.aws.region.static}")
    private val region: String,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String
) {

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .build()
    }

    fun getBucketName(): String = bucketName
}

