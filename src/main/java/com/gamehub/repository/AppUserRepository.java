package com.gamehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.AppUser;

/** Provides database operations for GameHub login accounts.  */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}