package com.bytebandit.gateway.service;

import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address.
     *
     * @param email the email address of the user to load
     * @return the UserEntity object representing the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserEntity loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new UsernameNotFoundException("User with provided" + email + " not found")
        );
    }
}
