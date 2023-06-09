package com.turkey.walkingwith7puppy.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkey.walkingwith7puppy.dto.response.ErrorResponse;
import com.turkey.walkingwith7puppy.exception.ErrorCode;
import com.turkey.walkingwith7puppy.exception.TokenErrorCode;
import com.turkey.walkingwith7puppy.repository.MemberRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.resolveToken(request, jwtUtil.ACCESS_KEY);
        String refreshToken = jwtUtil.resolveToken(request, jwtUtil.REFRESH_KEY);

        if(accessToken != null) {
            if(jwtUtil.validateToken(accessToken)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
            } else if(refreshToken != null && jwtUtil.refreshTokenValidation(refreshToken)) {
                String username = jwtUtil.getUserInfoFromToken(refreshToken);
                String newAccessToken = jwtUtil.createToken(username, "Access");
                jwtUtil.setHeaderAccessToken(response, newAccessToken);
                setAuthentication(username);
            } else if(refreshToken == null) {
                jwtExceptionHandler(response, TokenErrorCode.EXPIRED_ACCESS_TOKEN);
                return;
            } else {
                jwtExceptionHandler(response, TokenErrorCode.EXPIRED_REFRESH_TOKEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, ErrorCode errorCode) {

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper()
                .writeValueAsString(new ErrorResponse(
                    errorCode.name(),
                    errorCode.getHttpStatus().toString(),
                    errorCode.getMessage()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
