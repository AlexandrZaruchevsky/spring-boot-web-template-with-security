package ru.az.sandbox.security.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.az.sandbox.security.dto.security.ExceptionResponseV1;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException {
        final String token = getTokenFromRequest((HttpServletRequest) request);
        try {
	        if (token != null && jwtProvider.validateAccessToken(token)) {
	            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
	            		jwtProvider.getUsernameFromAccessToken(token), 
	            		null, jwtProvider.getAuthotorites(token));
	            SecurityContextHolder.getContext().setAuthentication(auth);
	        }
        }catch (AuthenticationCredentialsNotFoundException ex) {
        	 final var zResp = ((HttpServletResponse) response);
        	 zResp.setStatus(401);
        	 zResp.setContentType("application/json;charset=UTF-8");
        	 ExceptionResponseV1 exResp = new ExceptionResponseV1(
        			 HttpStatus.UNAUTHORIZED.value(), 
        			 HttpStatus.UNAUTHORIZED.name(), 
        			 ex.getLocalizedMessage());
        	 ObjectMapper om = new ObjectMapper();
        	 zResp.getWriter().print(om.writeValueAsString(exResp));
        }
        fc.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}