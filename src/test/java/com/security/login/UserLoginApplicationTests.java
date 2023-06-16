package com.security.login;

import com.security.jwt.domain.user.domain.User;
import com.security.jwt.domain.user.domain.UserRepository;
import com.security.jwt.enums.UserGrade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserLoginApplicationTests {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserLoginApplicationTests(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    void contextLoads() {
        User user = new User("234", bCryptPasswordEncoder.encode("234"), UserGrade.ADMIN);
        userRepository.save(user);
    }

}
