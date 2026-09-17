package com.example.club.controllers;

import com.example.club.dto.DTOs;
import com.example.club.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Capa CONTROLLER: alta de cuentas de Usuario (Socio o Admin).
 * <p>
 * TODA esta clase está restringida a ROLE_ADMIN con @PreAuthorize, en
 * línea con la consigna: "solo los administradores pueden registrar
 * usuarios; los socios ingresan una vez que se les haya creado una
 * cuenta". Además SecurityConfig protege las mismas rutas a nivel HTTP
 * como segunda capa de defensa (defense in depth).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra el formulario de alta. Se le agrega al Model un DTO vacío
     * ("registroDTO") para que Thymeleaf pueda hacer el data-binding con
     * th:object en la vista registro.html.
     */
    @GetMapping("/registro")
    public String formularioAlta(Model model) {
        model.addAttribute("registroDTO", new DTOs.AltaSocioDTO());
        model.addAttribute("activeMenu", "socios");
        return "registro";
    }

    /**
     * Procesa el alta. Si UsuarioService lanza una excepción de negocio
     * (correo duplicado, contraseñas que no coinciden, etc.) se vuelve a
     * mostrar el mismo formulario con el mensaje de error y los datos ya
     * cargados (registroDTO no se pierde porque Spring MVC lo vuelve a
     * poner en el Model al recibirlo como @ModelAttribute).
     */
    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("registroDTO") DTOs.AltaSocioDTO dto, Model model) {
        try {
            usuarioService.altaDeSocio(dto);
            return "redirect:/socios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activeMenu", "socios");
            return "registro";
        }
    }
}
