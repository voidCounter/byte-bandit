package com.bytebandit.userservice.config;

import com.bytebandit.userservice.mapper.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Configuration class for the project. */
@Configuration
public class ProjectConfig {
    private static final int STRENGTH = 12;

    /** Creates a password encoder bean. Uses BCrypt with strength 12. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }

    /** Creates a user mapper bean. */
    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }
}
