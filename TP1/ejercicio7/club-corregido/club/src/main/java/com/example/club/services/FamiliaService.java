package com.example.club.services;


import com.example.club.dto.DTOs;
import com.example.club.entities.Familia;
import com.example.club.entities.Socio;
import com.example.club.repositories.FamiliaRepository;
import com.example.club.repositories.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FamiliaService {

    @Autowired
    private FamiliaRepository familiaRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Transactional
    public Familia crearFamilia(DTOs.FamiliaDTO dto) {
        Familia familia = new Familia();
        familia.setApellido(dto.getApellido());
        return familiaRepository.save(familia);
    }

    @Transactional(readOnly = true)
    public List<Familia> listarFamilias() {
        return familiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Familia buscarPorId(Long id) throws Exception {
        return familiaRepository.findById(id).orElseThrow(() -> new Exception("Familia no encontrada"));
    }

    @Transactional
    public void modificarFamilia(Long id, DTOs.FamiliaDTO dto) throws Exception {
        Familia familia = buscarPorId(id);
        familia.setApellido(dto.getApellido());
        familiaRepository.save(familia);
    }

    @Transactional
    public void eliminarFamilia(Long id) {
        familiaRepository.deleteById(id);
    }

    /**
     * Asocia un Socio EXISTENTE (que todavía no pertenece a ningún grupo) a
     * la Familia como encargado/titular. Al setear socio.familia, el socio
     * pasa a formar parte de la lista "socios" (titulares) del grupo, tal
     * como se modela en Familia#socios (@OneToMany mappedBy = "familia").
     */
    @Transactional
    public void agregarEncargado(Long idFamilia, Long idSocio) throws Exception {
        Familia familia = buscarPorId(idFamilia);
        Socio socio = socioRepository.findById(idSocio)
                .orElseThrow(() -> new Exception("Socio no encontrado"));

        if (socio.getFamilia() != null) {
            throw new Exception("El socio ya pertenece a un grupo familiar");
        }

        socio.setFamilia(familia);
        socioRepository.save(socio);
    }
}
