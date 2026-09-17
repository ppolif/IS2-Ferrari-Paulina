package com.example.club.services;


import com.example.club.dto.DTOs;
import com.example.club.entities.Familia;
import com.example.club.entities.PagoCuota;
import com.example.club.entities.Socio;
import com.example.club.enums.EstadoPago;
import com.example.club.repositories.FamiliaRepository;
import com.example.club.repositories.PagoCuotaRepository;
import com.example.club.repositories.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagoCuotaService {

    @Autowired
    private PagoCuotaRepository pagoRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private FamiliaRepository familiaRepository;

    /**
     * Registra el pago de la cuota. Puede pagarse por Familia (grupo completo,
     * cubre a todos los integrantes) o por Socio individual (solo si el socio
     * NO pertenece a ningún grupo familiar).
     */
    @Transactional
    public void registrarPago(DTOs.PagoCuotaDTO dto) throws Exception {
        if (dto.getIdFamilia() == null && dto.getIdSocio() == null) {
            throw new Exception("Debe indicarse un socio o una familia para registrar el pago");
        }

        PagoCuota p = new PagoCuota();

        if (dto.getIdFamilia() != null) {
            Familia familia = familiaRepository.findById(dto.getIdFamilia())
                    .orElseThrow(() -> new Exception("Familia no encontrada"));
            p.setFamilia(familia);
        } else {
            Socio socio = socioRepository.findById(dto.getIdSocio())
                    .orElseThrow(() -> new Exception("Socio no encontrado"));
            if (socio.getFamilia() != null) {
                throw new Exception("El socio pertenece a un grupo familiar: el pago debe registrarse desde la familia");
            }
            p.setSocio(socio);
        }

        p.setMonto(dto.getMonto());
        p.setFormaPago(dto.getFormaPago());
        p.setComprobante(dto.getComprobante());
        p.setEstado(EstadoPago.PAGADA);
        p.setFechaPago(LocalDate.now());
        pagoRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<PagoCuota> listarTodos() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PagoCuota> listarPorSocio(Long idSocio) {
        return pagoRepository.findBySocio_IdOrderByFechaPagoDesc(idSocio);
    }

    @Transactional(readOnly = true)
    public List<PagoCuota> listarPorFamilia(Long idFamilia) {
        return pagoRepository.findByFamilia_IdOrderByFechaPagoDesc(idFamilia);
    }

    @Transactional(readOnly = true)
    public PagoCuota buscarPorId(Long id) throws Exception {
        return pagoRepository.findById(id).orElseThrow(() -> new Exception("Pago no encontrado"));
    }

    @Transactional
    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }
}
