package com.apuf.security;

import com.apuf.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Componente separado do SecurityConfig para evitar referência circular.
 *
 * O ciclo ocorria porque:
 *   SecurityConfig  →  injeta  →  JwtAuthFilter
 *   JwtAuthFilter   →  injeta  →  UserDetailsService  (que era Bean do SecurityConfig)
 *
 * Movendo o UserDetailsService para cá, o Spring consegue resolver as dependências
 * sem circular reference.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
