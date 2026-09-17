package com.example.club.services;


import com.example.club.entities.RegistroAcceso;
import com.example.club.repositories.AccesoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AccesoService {

    @Autowired
    private AccesoRepository accesoRepository;

    @Transactional
    public void registrarEntradaOSalida(Integer dni) throws Exception {
        // Busca si hay un acceso sin salida para este DNI hoy
        RegistroAcceso accesoAbierto = accesoRepository.findByFechaAndDniPersonaAndHoraSalidaIsNull(LocalDate.now(), dni);

        if (accesoAbierto != null) {
            // Registrar salida[cite: 6]
            accesoAbierto.setHoraSalida(LocalTime.now());
            accesoRepository.save(accesoAbierto);
        } else {
            // Registrar entrada[cite: 6]
            RegistroAcceso nuevoAcceso = new RegistroAcceso();
            nuevoAcceso.setDniPersona(dni);
            nuevoAcceso.setFecha(LocalDate.now());
            nuevoAcceso.setHoraEntrada(LocalTime.now());
            accesoRepository.save(nuevoAcceso);
        }
    }

    @Transactional(readOnly = true)
    public List<RegistroAcceso> listarPorFechaYFiltro(LocalDate fecha, String query) {
        if (query != null && !query.isEmpty()) {
            return accesoRepository.findByFechaAndDniPersona(fecha, Integer.parseInt(query));
        }
        return accesoRepository.findByFecha(fecha);
    }

    @Transactional(readOnly = true)
    public RegistroAcceso buscarPorId(Long id) throws Exception {
        return accesoRepository.findById(id).orElseThrow(() -> new Exception("Acceso no encontrado"));
    }
}
