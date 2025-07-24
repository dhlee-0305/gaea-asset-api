package com.gaea.asset.manager.common.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gaea.asset.manager.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
		this.jwtUtil = jwtUtil;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		String userId = null;
		String token = null;
		
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
			try {
				userId = jwtUtil.extractUserId(token);
			} catch (Exception e) {
				logger.error("JWT parsing failed: " + e.getMessage());
			}
		}

		if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

			if (jwtUtil.validateToken(token, userDetails.getUsername())) {
				UsernamePasswordAuthenticationToken authToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}