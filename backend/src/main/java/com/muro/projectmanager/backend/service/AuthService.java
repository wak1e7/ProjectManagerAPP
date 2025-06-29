package com.muro.projectmanager.backend.service;

import com.muro.projectmanager.backend.dto.*;
import com.muro.projectmanager.backend.model.*;
import com.muro.projectmanager.backend.repository.*;
import com.muro.projectmanager.backend.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder pwdEncoder;
    private final JwtTokenProvider jwtProvider;

    @Transactional
    public JwtAuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email ya está en uso");

        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(pwdEncoder.encode(req.getPassword()))
                .build();

        Role role = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER no configurado"));
        u.setRoles(Set.of(role));
        userRepo.save(u);

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        String token = jwtProvider.generateToken(auth);
        return new JwtAuthResponse(token);
    }

    public JwtAuthResponse authenticate(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        String token = jwtProvider.generateToken(auth);
        return new JwtAuthResponse(token);
    }
}
