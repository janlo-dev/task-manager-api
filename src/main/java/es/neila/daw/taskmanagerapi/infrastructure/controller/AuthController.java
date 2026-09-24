package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.*;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
import es.neila.daw.taskmanagerapi.infrastructure.config.JwtService;
import es.neila.daw.taskmanagerapi.infrastructure.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(UUID.randomUUID(), request.name(), request.email(), hashedPassword);
        userRepository.save(user);

        // El email de bienvenida no es crítico: si falla, el registro sigue adelante
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        } catch (MailException e) {
            log.error("Could not send welcome email to user {}", user.getId(), e);
        }

        String accessToken = jwtService.generateToken(user.getId());
        return new AuthResponse(user.getId(), accessToken);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user.getId());

        return new AuthResponse(user.getId(), accessToken);
    }
}
