package com.example.club.controllers;


import com.example.club.services.AccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Capa CONTROLLER: control de accesos (portería). Exclusivo de ADMIN, ya
 * que expone el listado completo de entradas/salidas de todos los
 * socios/familiares del club.
 */
@Controller
@RequestMapping("/accesos")
@PreAuthorize("hasRole('ADMIN')")
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    @GetMapping("/hoy")
    public String verAccesos(Model model,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                             @RequestParam(required = false) String query) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        // Usamos Model.addAttribute para llevar los datos a la bandeja de Thymeleaf[cite: 12]
        model.addAttribute("accesos", accesoService.listarPorFechaYFiltro(fecha, query));
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("query", query);
        model.addAttribute("activeMenu", "accesos");
        return "accesos_lista";
    }
}
