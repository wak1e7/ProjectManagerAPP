package com.muro.projectmanager.backend.security;

import com.muro.projectmanager.backend.model.User;
import com.muro.projectmanager.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    public CustomUserDetailsService(UserRepository userRepo) { this.userRepo = userRepo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email: " + email));
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con id: " + id));
        return UserPrincipal.create(user);
    }
}
