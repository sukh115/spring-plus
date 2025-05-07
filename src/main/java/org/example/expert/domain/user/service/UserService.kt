package org.example.expert.domain.user.service;

import io.jsonwebtoken.io.IOException;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.s3.S3Uploader;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserQueryRepository;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;
    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public void updateProfileImage(Long userId, String key) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("USER_NOT_FOUND"));
        user.updateProfileImage(key);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("USER_NOT_FOUND"));

        String key = user.getProfileImageUrl();
        if (key != null && !key.isBlank()) {
            s3Uploader.deleteProfileImage(userId, key);
            user.updateProfileImage(null); // DB에서도 제거
        }
    }

    public List<UserResponse> findUsersByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname);
    }

}
