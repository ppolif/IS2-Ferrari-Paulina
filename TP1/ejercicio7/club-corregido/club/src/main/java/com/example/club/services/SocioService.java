package com.example.club.services;


import com.example.club.entities.Socio;
import com.example.club.repositories.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Transactional(readOnly = true)
    public List<Socio> listarTodos() {
        return socioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Socio> buscarPorNombreODni(String query) {
        return socioRepository.findByNombreODni(query);
    }

    @Transactional(readOnly = true)
    public Socio buscarPorId(Long id) throws Exception {
        return socioRepository.findById(id).orElseThrow(() -> new Exception("Socio no encontrado"));
    }

    // Socios sin grupo familiar: usados como candidatos para asignar de "encargados"
    @Transactional(readOnly = true)
    public List<Socio> listarSinFamilia() {
        return socioRepository.findByFamiliaIsNull();
    }

    // Recupera el Socio ligado a la cuenta de Usuario actualmente logueada (Mi Perfil)
    @Transactional(readOnly = true)
    public Socio buscarPorCorreoUsuario(String correo) throws Exception {
        return socioRepository.findByUsuario_Correo(correo)
                .orElseThrow(() -> new Exception("No existe una ficha de Socio para este usuario"));
    }
}
