package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.s3.S3Uploader;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Uploader s3Uploader;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("file") MultipartFile file
    ) {
        String key = s3Uploader.uploadFile(file, "profile-images");
        userService.updateProfileImage(authUser.getId(), key);
        String imageUrl = s3Uploader.getFileUrl(key);
        return ResponseEntity.ok(imageUrl);
    }

}
