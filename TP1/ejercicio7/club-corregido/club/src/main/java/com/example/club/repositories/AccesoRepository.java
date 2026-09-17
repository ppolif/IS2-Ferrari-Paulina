package com.example.club.repositories;


import com.example.club.entities.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    List<RegistroAcceso> findByFecha(LocalDate fecha);

    List<RegistroAcceso> findByFechaAndDniPersona(LocalDate fecha, Integer dniPersona);

    RegistroAcceso findByFechaAndDniPersonaAndHoraSalidaIsNull(LocalDate fecha, Integer dniPersona);
}
