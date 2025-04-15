package com.bytebandit.gateway.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.exception.UserNotAuthenticatedException;
import lib.core.dto.response.ApiResponse;
import lib.user.model.UserEntityTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;


@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {
    
    @InjectMocks
    private UserLoginService userLoginService;
    
    /**
     * Test method to verify the behavior of getAuthenticatedUser method when the principal is
     * valid.
     */
    @Test
    void shouldReturnAuthenticatedUserDto_whenPrincipalIsValid() {
        UserEntityTemplate user = new UserEntityTemplate();
        user.setFullName("test user");
        user.setEmail("test@example.com");
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        
        ApiResponse<AuthenticatedUserDto> response =
            userLoginService.getAuthenticatedUser(authentication);
        
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals("Authenticated user confirmed.", response.getMessage());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals("test@example.com", response.getData().email());
        Assertions.assertEquals("test user", response.getData().fullName());
    }
    
    /**
     * Test method to verify the behavior of getAuthenticatedUser method when the principal is
     * invalid.
     */
    @Test
    void shouldThrowException_whenPrincipalIsInvalid() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenThrow(ClassCastException.class);
        
        assertThrows(UserNotAuthenticatedException.class, () ->
            userLoginService.getAuthenticatedUser(authentication));
    }
}

