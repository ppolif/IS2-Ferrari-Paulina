package com.example.colegio.controllers;

import com.example.colegio.dto.CambioClaveDTO;
import com.example.colegio.entities.Materia;
import com.example.colegio.services.AlumnoService;
import com.example.colegio.services.MateriaService;
import com.example.colegio.services.NotaService;
import com.example.colegio.services.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profesor")
@PreAuthorize("hasRole('PROFESOR')") // Seguridad por rol
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private NotaService notaService;

    @GetMapping("/alumnos/{materiaId}")
    public String listarAlumnosPorMateria(@PathVariable Long materiaId,
                                         Authentication auth,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Materia materia = materiaService.buscarPorId(materiaId);

            // Validar que el profesor autenticado sea el titular de la materia
            if (materia.getProfesor() == null || materia.getProfesor().getUsuario() == null
                    || !materia.getProfesor().getUsuario().getCorreo().equalsIgnoreCase(auth.getName())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para gestionar los alumnos de esta materia.");
                return "redirect:/materias";
            }

            model.addAttribute("materia", materia);
            model.addAttribute("alumnos", alumnoService.listarPorMateria(materiaId));
            model.addAttribute("notasPorAlumno", notaService.obtenerNotasPorMateriaAgrupadasPorAlumno(materiaId));
            return "alumnos_lista";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/materias";
        }
    }

    @PostMapping("/materias/{materiaId}/notas")
    public String registrarNota(@PathVariable Long materiaId,
                                @RequestParam Long alumnoId,
                                @RequestParam int valor,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            notaService.registrarNota(materiaId, alumnoId, valor, auth.getName());
            redirectAttributes.addFlashAttribute("exito", "Nota registrada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profesor/alumnos/" + materiaId;
    }

    @GetMapping("/cambiar-clave")
    public String mostrarCambioClave(Model model) {
        model.addAttribute("cambioClaveDTO", new CambioClaveDTO());
        return "cambio_clave";
    }

    @PostMapping("/cambiar-clave")
    public String procesarCambioClave(@ModelAttribute CambioClaveDTO dto, Authentication auth, Model model) {
        try {
            profesorService.cambiarClave(auth.getName(), dto);
            model.addAttribute("exito", "Contraseña actualizada exitosamente.");
            return "redirect:/materias";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cambio_clave";
        }
    }
}
