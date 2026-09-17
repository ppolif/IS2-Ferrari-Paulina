package com.example.club.controllers;

import com.example.club.dto.DTOs;
import com.example.club.entities.Familia;
import com.example.club.entities.Socio;
import com.example.club.services.FamiliaService;
import com.example.club.services.FamiliarService;
import com.example.club.services.PagoCuotaService;
import com.example.club.services.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Capa CONTROLLER: gestión de grupos Familiares.
 * <p>
 * Reglas de acceso (ver también SecurityConfig):
 *  - Alta de familia, asignar encargados y agregar familiares: solo ADMIN
 *    (@PreAuthorize a nivel de método).
 *  - Ver el detalle de una familia (/familias/{id}): ADMIN puede ver
 *    cualquiera; un SOCIO solo puede ver SU PROPIO grupo familiar. Esa
 *    verificación de "ownership" no se puede resolver con una simple
 *    regla de URL en SecurityConfig (depende de datos), así que se hace
 *    a mano dentro del Controller comparando el id de la familia del
 *    socio logueado contra el id solicitado.
 */
@Controller
@RequestMapping("/familias")
public class FamiliaController {

    @Autowired
    private FamiliaService familiaService;

    @Autowired
    private SocioService socioService;

    @Autowired
    private FamiliarService familiarService;

    @Autowired
    private PagoCuotaService pagoCuotaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarFamilias(Model model) {
        model.addAttribute("familias", familiaService.listarFamilias());
        model.addAttribute("activeMenu", "familias");
        return "familias_lista";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevaFamilia(Model model) {
        model.addAttribute("familiaDTO", new DTOs.FamiliaDTO());
        model.addAttribute("activeMenu", "familias");
        return "familia_form";
    }

    @PostMapping("/nueva")
    @PreAuthorize("hasRole('ADMIN')")
    public String crearFamilia(@ModelAttribute("familiaDTO") DTOs.FamiliaDTO dto) {
        Familia familia = familiaService.crearFamilia(dto);
        return "redirect:/familias/" + familia.getId();
    }

    /**
     * Redirección de conveniencia para el menú del SOCIO ("Mi Grupo
     * Familiar"): busca la familia del socio logueado y lo manda directo
     * a su propio detalle, sin que tenga que conocer el id.
     */
    @GetMapping("/mia")
    @PreAuthorize("hasRole('SOCIO')")
    public String miFamilia(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Socio socio = socioService.buscarPorCorreoUsuario(authentication.getName());
            if (socio.getFamilia() == null) {
                redirectAttributes.addFlashAttribute("info", "Todavía no pertenecés a ningún grupo familiar.");
                return "redirect:/socios/perfil";
            }
            return "redirect:/familias/" + socio.getFamilia().getId();
        } catch (Exception e) {
            return "redirect:/socios/perfil";
        }
    }

    @GetMapping("/{id}")
    public String detalleFamilia(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Familia familia = familiaService.buscarPorId(id);

            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!esAdmin) {
                // Un SOCIO solo puede ver el detalle de SU PROPIO grupo familiar.
                Socio socioLogueado = socioService.buscarPorCorreoUsuario(authentication.getName());
                boolean esSuFamilia = socioLogueado.getFamilia() != null
                        && socioLogueado.getFamilia().getId().equals(id);
                if (!esSuFamilia) {
                    return "redirect:/socios/perfil";
                }
            }

            model.addAttribute("familia", familia);
            model.addAttribute("pagos", pagoCuotaService.listarPorFamilia(id));
            model.addAttribute("activeMenu", "familias");

            // Datos auxiliares solo necesarios para el ADMIN (candidatos a
            // encargados y DTOs vacíos para los formularios de alta rápida).
            if (esAdmin) {
                model.addAttribute("sociosSinFamilia", socioService.listarSinFamilia());
                model.addAttribute("familiarDTO", new DTOs.FamiliarDTO());
            }

            return "familia_detalle";
        } catch (Exception e) {
            return "redirect:/familias";
        }
    }

    /**
     * Asocia un Socio existente (sin grupo) como encargado/titular del
     * grupo familiar indicado.
     */
    @PostMapping("/{id}/encargados")
    @PreAuthorize("hasRole('ADMIN')")
    public String agregarEncargado(@PathVariable Long id, @RequestParam Long idSocio,
                                    RedirectAttributes redirectAttributes) {
        try {
            familiaService.agregarEncargado(id, idSocio);
            redirectAttributes.addFlashAttribute("exito", "Encargado agregado al grupo familiar.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/familias/" + id;
    }

    /**
     * Agrega un integrante SIN cuenta de acceso propia (Familiar) al
     * grupo, por ejemplo hijos menores o familiares a cargo.
     */
    @PostMapping("/{id}/familiares")
    @PreAuthorize("hasRole('ADMIN')")
    public String agregarFamiliar(@PathVariable Long id,
                                   @ModelAttribute("familiarDTO") DTOs.FamiliarDTO dto,
                                   RedirectAttributes redirectAttributes) {
        try {
            dto.setIdFamilia(id);
            familiarService.crearFamiliar(dto);
            redirectAttributes.addFlashAttribute("exito", "Familiar agregado al grupo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/familias/" + id;
    }
}
