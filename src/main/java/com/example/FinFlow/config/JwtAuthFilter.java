package com.example.FinFlow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


// @Component - tells this class must be a bean
@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    // ONCE PER REQUEST FILTER - IS the class which CALLED ONCE PER EVERY REQUEST ON THE SERVER , implements genericBean
    // This class is the first thing the http request will meet
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ExpiredJwtException {
        // filter chain - contains a list of other filters we need to execute
        // authHeader - firstly we get our jwt token from header part AUTHORIZATION
        // this filter i do not to get expiced authorization to be checked and got an exception to expiration
        if(request.getServletPath().equals("/auth/login") || request.getServletPath().equals("/auth/token/refresh")){
            filterChain.doFilter(request,response);
            return; // didnt send return :DDD
        }

        final String authHeader =  request.getHeader("Authorization");

        String userEmail;
        final String jwtToken;
        System.out.println(authHeader);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken = authHeader.substring(7);
        try{
            userEmail = jwtService.extractEmail(jwtToken);
        }catch (ExpiredJwtException e){
            response.setStatus(403);
            response.setContentType(APPLICATION_JSON_VALUE);
            HashMap<String,String> object = new HashMap<>();
            object.put("error_message",e.getMessage());
            response.getOutputStream().print(new ObjectMapper().writeValueAsString(object));
            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if(jwtService.isTokenValid(jwtToken,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        filterChain.doFilter(request,response);
    }
}
