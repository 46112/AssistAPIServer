package com.help.stockassistplatform.global.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.help.stockassistplatform.domain.user.entity.User;
import com.help.stockassistplatform.domain.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private record WhiteListEntry(String method, String uriPattern) {
	}

	private static final List<WhiteListEntry> WHITE_LIST = List.of(
		new WhiteListEntry("GET", "/"),
		new WhiteListEntry("GET", "/favicon.ico"),
		new WhiteListEntry("GET", "/health"),
		new WhiteListEntry("GET", "/api/auth/verify"),
		new WhiteListEntry("POST", "/api/auth/login"),
		new WhiteListEntry("POST", "/api/auth/logout"),
		new WhiteListEntry("GET", "/api/reports")
	);

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		@NonNull final HttpServletRequest request,
		@NonNull final HttpServletResponse response,
		@NonNull final FilterChain filterChain) throws ServletException, IOException {

		final String requestUri = request.getRequestURI();
		final String method = request.getMethod();
		final String auth = request.getHeader("Authorization");

		if (isWhiteListed(method, requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (StringUtils.hasText(auth)) {
			log.info("인증 필터 시작: [{}]{}", method, requestUri);
			authenticateIfTokenValid(request);
			filterChain.doFilter(request, response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isWhiteListed(final String method, final String uri) {
		return WHITE_LIST.stream()
			.anyMatch(entry ->
				entry.method().equalsIgnoreCase(method)
					&& PatternMatchUtils.simpleMatch(entry.uriPattern(), uri)
			);
	}

	private void authenticateIfTokenValid(final HttpServletRequest request) {
		jwtUtil.extractAccessTokenFromRequest(request)
			.filter(jwtUtil::isTokenValidate)
			.flatMap(jwtUtil::extractUsername)
			.flatMap(userRepository::findWithProfileByUsername)
			.ifPresent(this::saveAuthentication);
	}

	private void saveAuthentication(final User myUser) {
		final UserDetails userDetails = CustomUser.from(myUser);
		final Authentication authentication =
			new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("Security Context에 '{}' 인증 정보를 저장", myUser.getUsername());
	}
}
