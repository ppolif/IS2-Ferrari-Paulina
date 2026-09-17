package com.example.club.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptación unidireccional matemática
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos de Mazer y la página de login: únicas rutas públicas.
                        // OJO: "/registro" y "/registrar" (alta de socio) NO son públicas: solo
                        // un ADMIN autenticado puede crear cuentas nuevas.
                        .requestMatchers("/assets/**", "/login", "/logincheck").permitAll()

                        // Panel administrativo: accesos, listado/alta de socios y familias.
                        .requestMatchers("/accesos/**").hasRole("ADMIN")
                        .requestMatchers("/registro", "/registrar").hasRole("ADMIN")
                        .requestMatchers("/pagos/**").hasRole("ADMIN")
                        .requestMatchers("/socios/perfil").hasRole("SOCIO")
                        .requestMatchers("/socios/**").hasRole("ADMIN")
                        .requestMatchers("/familias/mia").hasRole("SOCIO")
                        .requestMatchers("/familias/nueva", "/familias/*/encargados", "/familias/*/familiares").hasRole("ADMIN")

                        // El detalle de familia (/familias/{id}) lo puede pedir un ADMIN o un
                        // SOCIO: el filtrado por "es realmente SU familia" se hace a mano
                        // dentro de FamiliaController porque depende de datos, no de la URL.
                        // Las fotos (/img/**) solo requieren estar logueado.
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/logincheck")
                        .usernameParameter("correo")
                        .passwordParameter("clave")
                        // No forzamos siempre la misma URL: dejamos que "/" (ver AuthController)
                        // decida a dónde ir según el rol (ADMIN -> accesos, SOCIO -> perfil).
                        .defaultSuccessUrl("/", false)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // Desactivado para permitir POST de formularios sin token en dev[cite: 10]

        return http.build();
    }
}
