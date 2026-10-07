package com.example.clima.controllers;

import com.example.clima.services.ClimaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciudad")
@CrossOrigin(origins = "http://localhost:5173")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    //@CrossOrigin(origins = "http://localhost:5174/")
    @GetMapping("/clima/{ciudad}")
    public ResponseEntity<String> obtenerClima(@PathVariable String ciudad) {
        return climaService.obtenerClimaApi(ciudad);

    }
}
