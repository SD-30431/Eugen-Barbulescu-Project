package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.AuthResponseDTO;
import com.example.sd_backend2.dto.LoginRequestDTO;
import com.example.sd_backend2.dto.SignupRequestDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.AuthActivity;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.AuthActivityRepository;
import com.example.sd_backend2.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthActivityRepository authActivityRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupSuccess() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.name = "newUser";
        request.password = "password";

        when(authorRepository.findByName("newUser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        ResponseEntity<?> response = authService.signup(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Signup successful", response.getBody());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testSignupUserAlreadyExists() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.name = "existingUser";
        request.password = "password";

        Author existingAuthor = new Author("existingUser", "hashed", false);
        when(authorRepository.findByName("existingUser")).thenReturn(existingAuthor);

        ResponseEntity<?> response = authService.signup(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.name = "testUser";
        request.password = "testPass";

        Author author = new Author("testUser", "hashedPass", false);
        author.setAuthorId(1L);

        // Instead of doNothing(), simulate a successful authentication by returning a token object
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("testUser", "testPass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(authorRepository.findByName("testUser")).thenReturn(author);
        when(jwtTokenUtil.generateToken(any())).thenReturn("mockToken");

        ResponseEntity<?> response = authService.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthResponseDTO);

        AuthResponseDTO body = (AuthResponseDTO) response.getBody();
        assertEquals("mockToken", body.token);
        assertEquals(1L, body.authorId);
        assertEquals("USER", body.role);
        verify(authActivityRepository, times(1)).save(any(AuthActivity.class));
    }

    @Test
    void testLoginBadCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.name = "testUser";
        request.password = "wrongPass";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authService.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
        verify(authActivityRepository, never()).save(any(AuthActivity.class));
    }

    @Test
    void testLogout() {
        String token = "Bearer mockToken";
        Author author = new Author("testUser", "hashedPass", false);
        author.setAuthorId(1L);

        when(jwtTokenUtil.getUsernameFromToken("mockToken")).thenReturn("testUser");
        when(authorRepository.findByName("testUser")).thenReturn(author);

        ResponseEntity<?> response = authService.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout recorded", response.getBody());
        verify(authActivityRepository, times(1)).save(any(AuthActivity.class));
    }

    @Test
    void testLogoutNoBearer() {
        String token = "InvalidHeader";

        ResponseEntity<?> response = authService.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout recorded", response.getBody());
        verify(authActivityRepository, never()).save(any(AuthActivity.class));
    }
}
