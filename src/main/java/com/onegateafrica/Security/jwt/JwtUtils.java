package com.onegateafrica.Security.jwt;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import com.onegateafrica.Entities.Role;
import com.onegateafrica.ServiceImpl.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

//import com.onegateafrica.serviceImpl.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


	@Value("${bezkoder.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	private static SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); ;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		 //or HS384 or HS512
		return Jwts.builder()
				.setSubject((userPrincipal.getEmail()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key)
				.claim("roles", userPrincipal.getAuthorities())
				.compact();
	}

	public String generateJwtToken(String email, Set<Role> roles) {

		//key = Keys.secretKeyFor(SignatureAlgorithm.HS512); //or HS384 or HS512
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key)
				.claim("roles",roles)
				.compact();
	}


	public String getUserNameFromJwtToken(String token) {
		//  key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512

		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}
	public Jws<Claims> parseJwtToken(String authToken){
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
	}
	public boolean validateJwtToken(String authToken) {
		 //SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
