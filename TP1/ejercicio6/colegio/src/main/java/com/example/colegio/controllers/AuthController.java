package com.example.colegio.controllers;

import com.example.colegio.dto.RegistroAlumnoDTO;
import com.example.colegio.dto.RegistroProfesorDTO;
import com.example.colegio.services.AlumnoService;
import com.example.colegio.services.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroProfesorDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarProfesor(@ModelAttribute("registroDTO") RegistroProfesorDTO dto, Model model) {
        try {
            profesorService.registrarProfesor(dto);
            return "redirect:/login?exito";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Error en el registro");
            return "registro";
        }
    }

    @GetMapping("/registro-alumno")
    public String mostrarRegistroAlumno(Model model) {
        model.addAttribute("registroAlumnoDTO", new RegistroAlumnoDTO());
        return "registro_alumno";
    }

    @PostMapping("/registro-alumno")
    public String registrarAlumno(@ModelAttribute RegistroAlumnoDTO dto, RedirectAttributes attributes) {
        try {
            alumnoService.registrarAlumno(dto);
            return "redirect:/login?exito";
        } catch (Exception e) {
            attributes.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Error al registrar alumno");
            return "redirect:/registro-alumno";
        }
    }
}
