package com.help.stockassistplatform.global.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
	private static final String ACCESS_HEADER = "Authorization";
	private static final String ACCESS_TOKEN = "AccessToken";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final Long ACCESS_TOKEN_EXP = (long)(60 * 60 * 1000); // 60분
	private static final Long REFRESH_TOKEN_EXP = (long)(7 * 24 * 60 * 60 * 1000); // 7일
	private static final String REFRESH_TOKEN = "RefreshToken";
	private SecretKey secretKey;

	@Value("${jwt.secret}")
	private String secretKeyBase64;

	@PostConstruct
	public void init() {
		final byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
		secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(final CustomUser user) {
		final var authorities = user.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		final String accessToken = Jwts.builder()
			.claim("username", user.getUsername())
			.claim("nickname", user.getNickname())
			.claim("authorities", authorities)
			.subject(ACCESS_TOKEN)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
			.signWith(secretKey)
			.compact();
		return TOKEN_PREFIX + accessToken;
	}

	public String createRefreshToken(final CustomUser user) {
		return Jwts.builder()
			.claim("username", user.getUsername())
			.subject(REFRESH_TOKEN)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
			.signWith(secretKey)
			.compact();
	}

	public boolean isTokenValidate(final String token) {
		try {
			return !isTokenExpired(parseToken(token));
		} catch (final JwtException e) {
			log.error("JWT 토큰 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public Optional<String> extractUsername(String token) {
		try {
			return Optional.ofNullable(parseToken(token).get("username", String.class));
		} catch (final JwtException e) {
			return Optional.empty();
		}
	}

	public ResponseCookie createRefreshTokenCookie(final String refreshToken) {
		return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(REFRESH_TOKEN_EXP / 1000L)
			.build();
	}

	public Optional<String> extractRefreshTokenFromRequest(final HttpServletRequest request) {
		if (null == request.getCookies()) {
			return Optional.empty();
		}
		for (final Cookie cookie : request.getCookies()) {
			if (REFRESH_TOKEN.equals(cookie.getName())) {
				return Optional.of(cookie.getValue());
			}
		}
		return Optional.empty();
	}

	public Optional<String> extractAccessTokenFromRequest(final HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
			.filter(accessToken -> {
				log.info("accessToken: " + accessToken);
				return accessToken.startsWith(TOKEN_PREFIX);
			})
			.map(accessToken -> accessToken.substring(TOKEN_PREFIX.length()));
	}

	public ResponseCookie createExpiredRefreshTokenCookie() {
		return ResponseCookie.from(REFRESH_TOKEN, "")
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(0L) // 쿠키 즉시 만료
			.build();
	}

	private Claims parseToken(final String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token.trim())
			.getPayload();
	}

	private boolean isTokenExpired(final Claims claims) {
		final Date expiration = claims.getExpiration();
		final Date now = new Date();
		log.info("현재 시간: " + now);
		log.info("토큰 만료: " + expiration);
		return claims.getExpiration().before(now);
	}
}
