package com.example.FinFlow.service;

import com.example.FinFlow.additional.AuthenticationResponse;
import com.example.FinFlow.additional.RegisterRequest;
import com.example.FinFlow.additional.Response;
import com.example.FinFlow.config.JwtService;
import com.example.FinFlow.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Random;

@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationService(PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    public Response register(RegisterRequest request) {
        Random rd = new Random();
        User user = new User(
                request.getUsername() == null ? "User " + rd.nextInt(100) : request.getUsername(),
                LocalDate.now(),
                request.getEmail(),
                request.getNumber(),
                passwordEncoder.encode(request.getPassword())
        );

        return userService.addNewUser(user);

    }
    public AuthenticationResponse login(RegisterRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        User u = userService.findByEmail(request.getEmail()).orElseThrow();
        String access = jwtService.generateToken(User.toUserDetails(u),3);
        String refresh = jwtService.generateToken(User.toUserDetails(u),72);

        return new AuthenticationResponse(access,refresh);
    }
    public boolean activate(String code) {
        return userService.verify(code);
    }
}
