package org.example.expert.domain.user.controller

import lombok.RequiredArgsConstructor
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.s3.S3Uploader
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(
    private val userService: UserService,
    private val s3Uploader: S3Uploader
) {
    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @PutMapping("/users")
    fun changePassword(authUser: AuthUser, @RequestBody userChangePasswordRequest: UserChangePasswordRequest) {
        userService.changePassword(authUser.id, userChangePasswordRequest)
    }

    @PostMapping("/users/profile-image")
    fun uploadProfileImage(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<String> {
        val key = s3Uploader.uploadFile(file, "profile-images")
        userService.updateProfileImage(authUser.id, key)
        val imageUrl = s3Uploader.getFileUrl(key)
        return ResponseEntity.ok(imageUrl)
    }

    @DeleteMapping("/users/profile-image")
    fun deleteProfileImage(@AuthenticationPrincipal authUser: AuthUser): ResponseEntity<Void> {
        userService.deleteProfileImage(authUser.id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/users/list")
    fun getUsersByNickName(@RequestParam nickname: String): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.findUsersByNickname(nickname))
    }
}
