package com.gamehub.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gamehub.entity.AppUser;
import com.gamehub.repository.AppUserRepository;

/** Loads login accounts from the database for Spring Security. */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPasswordHash())
                .roles(appUser.getRole().name())
                .disabled(!appUser.isEnabled())
                .build();
    }
}