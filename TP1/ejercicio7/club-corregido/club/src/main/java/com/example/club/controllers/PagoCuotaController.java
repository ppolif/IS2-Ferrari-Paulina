package com.example.club.controllers;

import com.example.club.dto.DTOs;
import com.example.club.entities.Familia;
import com.example.club.entities.Socio;
import com.example.club.services.FamiliaService;
import com.example.club.services.PagoCuotaService;
import com.example.club.services.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Capa CONTROLLER: registro del pago de la cuota del club.
 * <p>
 * El pago SIEMPRE se dispara desde un "origen" concreto y ya conocido:
 *   - el detalle de una FAMILIA (/familias/{id}) si el socio pertenece a
 *     un grupo familiar -> el pago cubre a todo el grupo, o
 *   - el detalle de un SOCIO (/socios/{id}) si ese socio NO pertenece a
 *     ningún grupo familiar -> el pago es individual.
 * Por eso este Controller recibe idFamilia XOR idSocio por query param,
 * arma el formulario con el destino ya fijo (no hay combo para elegir
 * "cualquier" socio) y, al confirmar, vuelve a redirigir al detalle de
 * origen para que el usuario vea el pago reflejado (historial de pagos).
 * <p>
 * Solo ADMIN puede registrar pagos: es una operación administrativa de
 * cobranza, igual que el alta de socios.
 */
@Controller
@RequestMapping("/pagos")
@PreAuthorize("hasRole('ADMIN')")
public class PagoCuotaController {

    @Autowired
    private PagoCuotaService pagoCuotaService;

    @Autowired
    private SocioService socioService;

    @Autowired
    private FamiliaService familiaService;

    @GetMapping("/nuevo")
    public String formularioPago(@RequestParam(required = false) Long idSocio,
                                  @RequestParam(required = false) Long idFamilia,
                                  Model model) {
        try {
            DTOs.PagoCuotaDTO dto = new DTOs.PagoCuotaDTO();
            String nombreDestino;

            if (idFamilia != null) {
                Familia familia = familiaService.buscarPorId(idFamilia);
                dto.setIdFamilia(idFamilia);
                nombreDestino = "Grupo Familiar " + familia.getApellido();
            } else if (idSocio != null) {
                Socio socio = socioService.buscarPorId(idSocio);
                dto.setIdSocio(idSocio);
                nombreDestino = socio.getNombre() + " " + socio.getApellido() + " (Socio individual)";
            } else {
                // No se indicó ningún destino: no hay desde dónde volver, así
                // que no tiene sentido mostrar el formulario "al aire".
                return "redirect:/socios";
            }

            model.addAttribute("pagoDTO", dto);
            model.addAttribute("nombreDestino", nombreDestino);
            model.addAttribute("activeMenu", idFamilia != null ? "familias" : "socios");
            return "pago_cuota";
        } catch (Exception e) {
            return "redirect:/socios";
        }
    }

    @PostMapping("/registrar")
    public String registrarPago(@ModelAttribute("pagoDTO") DTOs.PagoCuotaDTO dto,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            pagoCuotaService.registrarPago(dto);
            redirectAttributes.addFlashAttribute("exito", "Pago registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Volvemos siempre al detalle de origen (familia o socio individual)
        if (dto.getIdFamilia() != null) {
            return "redirect:/familias/" + dto.getIdFamilia();
        }
        return "redirect:/socios/" + dto.getIdSocio();
    }
}
