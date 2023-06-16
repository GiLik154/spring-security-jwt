package com.security.jwt.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);

    default User findUserByUsername(String username){
        return findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("Login Failed"));
    }
}
