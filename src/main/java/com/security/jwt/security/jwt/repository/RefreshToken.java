package com.security.jwt.security.jwt.repository;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(length = 1000)
    private String token;

    protected RefreshToken() {
    }

    public RefreshToken(String username) {
        this.username = username;
    }

    public void registerToken(String token) {
        this.token = token;
    }
}