package com.example.club.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Capa CONTROLLER: rutas de autenticación / punto de entrada.
 * <p>
 * - GET /login: sirve la vista pública de login (permitAll en
 *   SecurityConfig). Spring Security la usa como "loginPage".
 * - GET /: página raíz protegida (requiere estar autenticado, ver
 *   SecurityConfig#anyRequest().authenticated()). Redirige a la pantalla
 *   inicial correspondiente según el ROL de la sesión, para que ADMIN y
 *   SOCIO nunca vean el mismo "home":
 *      ADMIN -> /accesos/hoy   (panel de control de accesos)
 *      SOCIO -> /socios/perfil (su propia ficha)
 * <p>
 * Esta clase no tiene lógica de negocio: solo decide a qué vista/URL
 * dirigir, por eso no depende de ningún Service.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN"));

        return esAdmin ? "redirect:/accesos/hoy" : "redirect:/socios/perfil";
    }
}
