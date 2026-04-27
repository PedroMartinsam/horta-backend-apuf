package com.apuf.service;

import com.apuf.dto.AuthDTO;
import com.apuf.model.Usuario;
import com.apuf.repository.UsuarioRepository;
import com.apuf.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.generateToken(usuario);
        return buildResponse(usuario, token);
    }

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Usuario usuario = Usuario.builder()
            .nome(request.getNome())
            .email(request.getEmail())
            .senha(passwordEncoder.encode(request.getPassword()))
            .telefone(request.getTelefone())
            .role(Usuario.Role.CLIENTE)
            .ativo(true)
            .build();

        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuario);
        return buildResponse(usuario, token);
    }

    private AuthDTO.AuthResponse buildResponse(Usuario usuario, String token) {
        AuthDTO.AuthResponse.UserInfo userInfo = new AuthDTO.AuthResponse.UserInfo();
        userInfo.setId(usuario.getId());
        userInfo.setNome(usuario.getNome());
        userInfo.setEmail(usuario.getEmail());
        userInfo.setRole(usuario.getRole().name());

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse();
        response.setToken(token);
        response.setUser(userInfo);
        return response;
    }
}
