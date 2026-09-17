package com.example.club.controllers;

import com.example.club.entities.Socio;
import com.example.club.services.PagoCuotaService;
import com.example.club.services.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Capa CONTROLLER: gestión de Socios.
 * <p>
 * - Listado y detalle "por id" (/socios, /socios/{id}): exclusivos de
 *   ADMIN, protegidos con @PreAuthorize a nivel de método (y reforzado a
 *   nivel de URL en SecurityConfig). Es el panel de administración.
 * - /socios/perfil: accesible para cualquier SOCIO logueado, siempre
 *   muestra la ficha del socio DUEÑO de la sesión actual (nunca recibe un
 *   id por parámetro, así que un socio no puede ver la ficha de otro
 *   simplemente cambiando la URL).
 */
@Controller
@RequestMapping("/socios")
public class SocioController {

    @Autowired
    private SocioService socioService;

    @Autowired
    private PagoCuotaService pagoCuotaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarSocios(Model model, @RequestParam(required = false) String q) {
        if (q != null && !q.isEmpty()) {
            model.addAttribute("socios", socioService.buscarPorNombreODni(q));
        } else {
            model.addAttribute("socios", socioService.listarTodos());
        }
        model.addAttribute("query", q);
        model.addAttribute("activeMenu", "socios");
        return "socios_lista";
    }

    @GetMapping("/perfil")
    @PreAuthorize("hasRole('SOCIO')")
    public String miPerfil(Model model, Authentication authentication) {
        try {
            Socio socio = socioService.buscarPorCorreoUsuario(authentication.getName());
            model.addAttribute("socio", socio);
            model.addAttribute("pagos", pagoCuotaService.listarPorSocio(socio.getId()));
            model.addAttribute("esMiPerfil", true);
            model.addAttribute("activeMenu", "perfil");
            return "socio_detalle";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detalleSocio(@PathVariable Long id, Model model) {
        try {
            Socio socio = socioService.buscarPorId(id);
            model.addAttribute("socio", socio);
            model.addAttribute("pagos", pagoCuotaService.listarPorSocio(id));
            model.addAttribute("esMiPerfil", false);
            model.addAttribute("activeMenu", "socios");
            return "socio_detalle";
        } catch (Exception e) {
            return "redirect:/socios";
        }
    }
}
