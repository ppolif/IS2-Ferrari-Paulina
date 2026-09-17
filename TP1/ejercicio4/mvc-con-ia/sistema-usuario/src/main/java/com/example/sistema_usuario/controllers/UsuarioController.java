package com.example.sistema_usuario.controllers;


import com.example.sistema_usuario.model.domain.Usuario;
import com.example.sistema_usuario.model.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * CAPA DE CONTROLADOR (Controller)
 * @Controller: Marca esta clase como un controlador MVC clásico de Spring.
 * Su objetivo es atrapar peticiones HTTP, usar el Service y retornar plantillas Thymeleaf.
 */
@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

// ==========================================
    // INGRESO Y SESIÓN
    // ==========================================

    @GetMapping({"/", "/login"})
    public String mostrarLogin(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login";
    }

    /**
     * Agregamos HttpSession como parámetro. Spring Boot inyecta la sesión automáticamente.
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String clave,
                                Model model,
                                HttpSession session) {
        try {
            // Recibimos el OBJETO Usuario
            Usuario usuarioLogueado = usuarioService.autenticar(correo, clave);

            // ERROR CORREGIDO: Guardamos el objeto completo bajo el nombre "usuariosession"
            session.setAttribute("usuariosession", usuarioLogueado);

            return "redirect:/home";

        } catch (Exception e) {
            String mensajeError = e.getMessage();

            if ("no_registrado".equals(mensajeError)) {
                model.addAttribute("error", "Usuario no encontrado. Por favor, regístrese.");
                model.addAttribute("mostrarRegistro", true);
                return "login";
            } else if ("cuenta_bloqueada".equals(mensajeError)) {
                // Pasamos el texto al Model y retornamos la vista (evita el Error 400)
                model.addAttribute("error", "Cuenta bloqueada por múltiples intentos fallidos.");
                return "login";
            } else if ("clave_incorrecta".equals(mensajeError)) {
                model.addAttribute("error", "Contraseña incorrecta. Se ha registrado un intento fallido.");
                return "login";
            }
        }

        model.addAttribute("error", "Error inesperado al intentar iniciar sesión.");
        return "login";
    }

    /**
     * Cerrar sesión. Destruye los datos guardados.
     */
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        // Invalida (borra) la sesión actual
        session.invalidate();
        return "redirect:/login?error=Sesion cerrada exitosamente.";
    }

    // ==========================================
    // RUTAS PROTEGIDAS
    // ==========================================

    /**
     * Ahora la ruta /home está protegida. Solo se puede entrar si hay sesión.
     */
    @GetMapping("/home")
    public String mostrarHome(HttpSession session) {
        // Validamos buscando la misma etiqueta que usamos en el login
        if (session.getAttribute("usuariosession") == null) {
            return "redirect:/login";
        }
        return "home";
    }

    // ==========================================
    // REGISTRO (Se mantiene igual)
    // ==========================================

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
                                   @RequestParam String repeticionClave,
                                   Model model) {
        try {
            // Validación Backend de DNI
            if (usuario.getDni() < 10000000) {
                model.addAttribute("error", "El DNI debe ser mayor a 10.000.000");
                return "registro";
            }

            // Validación Backend de Fecha
            int anioNacimiento = usuario.getFechaNac().getYear(); // Dependiendo de si usas Date o LocalDate
            if (anioNacimiento < 1930 || anioNacimiento >= 2026) {
                model.addAttribute("error", "El año de nacimiento debe estar entre 1930 y 2025");
                return "registro";
            }

            usuarioService.registrarUsuario(usuario, repeticionClave);
            return "redirect:/login?error=Registro exitoso. Inicie sesion.";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
}
