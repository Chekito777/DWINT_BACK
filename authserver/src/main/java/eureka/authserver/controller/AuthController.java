package eureka.authserver.controller;

import eureka.authserver.dto.LoginRequest;
import eureka.authserver.dto.LoginResponse;
import eureka.authserver.dto.UserDto;
import eureka.authserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtEncoder jwtEncoder,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Instant now = Instant.now();
        long expiry = 3600L;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8083")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(token, "Bearer", expiry));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null ||
            request.getUsername().isBlank() || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Usuario y contrasena son requeridos");
        }
        UserDto user = userService.registerUser(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDto> userinfo(Authentication authentication) {
        UserDto user;
        if (authentication == null) {
            user = new UserDto();
            user.setUsername("guest");
            user.setId(0L);
            user.setRoles(new java.util.HashSet<>(java.util.Arrays.asList("USER")));
        } else {
            user = userService.findByUsername(authentication.getName());
        }
        return ResponseEntity.ok(user);
    }
}
