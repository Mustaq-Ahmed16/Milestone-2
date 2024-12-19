package in.projectjwt.main.services;


import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.InvalidCredentialsException;
import in.projectjwt.main.exceptions.UserNotFoundException;
import in.projectjwt.main.repositories.UserRepository;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private LoginUserDto loginUserDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        user = new User();
        user.setId(1);
        user.setFullName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("Password@123");
        user.setAddress("123 Main St");
        user.setPhone("1234567890");

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("johndoe@example.com");
        loginUserDto.setPassword("Password@123");
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = authenticationService.register(user);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already created with the same email ID.", response.getBody().get("message"));
    }

    @Test
    public void testRegister_SuccessfulRegistration() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<Map<String, String>> response = authenticationService.register(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody().get("message"));
    }

    @Test
    public void testAuthenticate_SuccessfulAuthentication() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Mock authentication success (no exception is thrown)
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals(user.getEmail(), authenticatedUser.getEmail());
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Mock authentication failure (throw BadCredentialsException)
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(loginUserDto));
    }

    @Test
    public void testAuthenticate_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Mock authentication (no exception is thrown)
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(loginUserDto));
    }
}
