package com.help.stockassistplatform.domain.user.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.help.stockassistplatform.domain.user.dto.request.ProfileUpdateRequestDto;
import com.help.stockassistplatform.domain.user.dto.response.ProfileResponseDto;
import com.help.stockassistplatform.domain.user.entity.User;
import com.help.stockassistplatform.domain.user.service.UserService;
import com.help.stockassistplatform.global.common.response.ApiResponse;
import com.help.stockassistplatform.global.jwt.CustomUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	// 프로필 조회
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ApiResponse<?> lookUpUserProfile(
		@AuthenticationPrincipal final CustomUser userDetail
	) {
		final User loginUser = userService.findUserByUsername(userDetail.getUsername());
		final ProfileResponseDto profileInfo = ProfileResponseDto.from(loginUser.getUserProfile());
		return ApiResponse.success(profileInfo);
	}

	// 프로필 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/me")
	public ApiResponse<?> updateUserProfile(
		@AuthenticationPrincipal final CustomUser userDetail,
		@Valid @RequestBody final ProfileUpdateRequestDto requestDto
	) {
		final User loginUser = userService.findUserByUsername(userDetail.getUsername());
		userService.updateUserProfile(loginUser, requestDto);
		return ApiResponse.success(null);
	}

	// 비밀번호 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/me/password")
	public void updateUserPassword() {
	}
}
