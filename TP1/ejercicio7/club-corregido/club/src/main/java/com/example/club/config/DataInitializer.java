package com.example.club.config;

import com.example.club.entities.Usuario;
import com.example.club.enums.Rol;
import com.example.club.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa de configuración: resuelve el problema del "huevo y la gallina" de
 * la seguridad del sistema.
 * <p>
 * Como /registro y /registrar están protegidos con hasRole('ADMIN') (ver
 * SecurityConfig), NO existe ninguna pantalla para dar de alta al PRIMER
 * administrador: hace falta ya estar logueado como ADMIN para crear
 * cuentas. Insertar el usuario directamente en la base de datos no sirve
 * porque el campo "clave" se guarda con BCrypt (ver
 * SecurityConfig#passwordEncoder), y un valor de texto plano insertado a
 * mano nunca va a matchear contra el hash que espera Spring Security al
 * loguearse.
 * <p>
 * Este CommandLineRunner corre una única vez al arrancar la aplicación:
 * si todavía no existe NINGÚN Usuario con rol ADMIN (activo), crea uno
 * "semilla" usando el mismo PasswordEncoder que usa el resto del sistema
 * (UsuarioService#altaDeSocio), así el hash queda 100% compatible con el
 * login normal. Las credenciales son configurables por variables de
 * entorno / application.properties (club.admin.correo, club.admin.clave)
 * para no dejar una contraseña fija hardcodeada en el código fuente.
 * <p>
 * Una vez logueado con este admin semilla, se pueden crear el resto de
 * los administradores y socios desde /registro con total normalidad.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${club.admin.correo:admin@club.com}")
    private String correoAdminSemilla;

    @Value("${club.admin.clave:admin1234}")
    private String claveAdminSemilla;

    @Override
    @Transactional
    public void run(String... args) {
        boolean yaExisteAdmin = usuarioRepository.existsByRolAndEliminadoFalse(Rol.ADMIN);
        if (yaExisteAdmin) {
            return; // Ya hay al menos un administrador: no hace falta sembrar nada.
        }

        Usuario admin = new Usuario();
        admin.setCorreo(correoAdminSemilla);
        admin.setClave(passwordEncoder.encode(claveAdminSemilla)); // Hash BCrypt, igual que en el alta normal
        admin.setRol(Rol.ADMIN);
        admin.setEliminado(false);
        usuarioRepository.save(admin);

        log.warn("======================================================================");
        log.warn(" No había ningún ADMINISTRADOR cargado: se creó uno por defecto.");
        log.warn(" Correo:      {}", correoAdminSemilla);
        log.warn(" Contraseña:  {}", claveAdminSemilla);
        log.warn(" Iniciá sesión con estos datos y cambiá la contraseña / creá tus propios");
        log.warn(" administradores desde /registro lo antes posible.");
        log.warn("======================================================================");
    }
}
