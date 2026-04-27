package com.apuf.controller;

import com.apuf.dto.AuthDTO;
import com.apuf.model.Usuario;
import com.apuf.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO.AuthResponse> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        AuthDTO.AuthResponse.UserInfo info = new AuthDTO.AuthResponse.UserInfo();
        info.setId(usuario.getId());
        info.setNome(usuario.getNome());
        info.setEmail(usuario.getEmail());
        info.setRole(usuario.getRole().name());
        return ResponseEntity.ok(info);
    }
}
