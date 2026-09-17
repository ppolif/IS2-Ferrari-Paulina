package com.example.club.services;

import com.example.club.dto.DTOs;
import com.example.club.entities.Familiar;
import com.example.club.repositories.FamiliarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FamiliarService {

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private FamiliaService familiaService;

    @Transactional
    public void crearFamiliar(DTOs.FamiliarDTO dto) throws Exception {
        Familiar f = new Familiar();
        f.setNombre(dto.getNombre());
        f.setApellido(dto.getApellido());
        f.setDni(dto.getDni());
        f.setParentesco(dto.getParentesco());
        f.setFamilia(familiaService.buscarPorId(dto.getIdFamilia()));
        familiarRepository.save(f);
    }

    @Transactional(readOnly = true)
    public List<Familiar> listarTodos() {
        return familiarRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Familiar buscarPorId(Long id) throws Exception {
        return familiarRepository.findById(id).orElseThrow(() -> new Exception("Familiar no encontrado"));
    }

    @Transactional
    public void actualizarFamiliar(Long id, DTOs.FamiliarDTO dto) throws Exception {
        Familiar f = buscarPorId(id);
        f.setNombre(dto.getNombre());
        f.setApellido(dto.getApellido());
        f.setDni(dto.getDni());
        f.setParentesco(dto.getParentesco());
        familiarRepository.save(f);
    }

    @Transactional
    public void eliminarFamiliar(Long id) {
        familiarRepository.deleteById(id);
    }
}
