package lab.microservices.usuario.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lab.microservices.usuario.api.dto.AuthRequest;
import lab.microservices.usuario.api.dto.AuthResponse;
import lab.microservices.usuario.api.dto.RegisterRequest;
import lab.microservices.usuario.security.AuthUser;
import lab.microservices.usuario.security.AuthUserRepository;
import lab.microservices.usuario.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação - endpoints públicos para login e registro
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints de autenticação JWT")
public class AuthController {

    private final AuthUserRepository authUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registrando novo usuário: {}", request.getUsername());

        // Validações
        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já existe");
        }
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Criar usuário
        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AuthUser.Role.USER)
                .enabled(true)
                .build();

        authUserRepository.save(user);

        // Gerar token
        String token = jwtService.generateToken(user);

        log.info("Usuário registrado com sucesso: {}", user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400)
                .username(user.getUsername())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Tentativa de login: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        AuthUser user = authUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);

        log.info("Login bem-sucedido: {}", user.getUsername());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400)
                .username(user.getUsername())
                .build());
    }
}
