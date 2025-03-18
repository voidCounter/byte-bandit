package com.bytebandit.userservice.service;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.mapper.UserMapper;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/** Service for user registration. */
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final UserMapper userMapper;

    /**
     * Registers a new user.
     *
     * @param registrationRequest the registration request.
     *
     * @return the registration response.
     * @throws UserAlreadyExistsException if a user with the provided email already exists.
     */
    public UserRegistrationResponse register(
        UserRegistrationRequest registrationRequest
    ) {
        UserEntity createdUser = UserEntity.builder()
            .email(registrationRequest.getEmail())
            .passwordHash(
                passwordEncoder.encode(registrationRequest.getPassword())
            )
            .fullName(registrationRequest.getFullName())
            .verified(false)
            .build();
        try {
            UserEntity savedUser = transactionTemplate.execute(
                result -> userRepository.save(createdUser)
            );
            return userMapper.toUserRegistrationResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with provided email already exists.", e);
        }
    }
}
