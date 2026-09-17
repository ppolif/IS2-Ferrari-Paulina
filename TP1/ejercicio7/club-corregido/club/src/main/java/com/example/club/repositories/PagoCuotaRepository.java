package com.example.club.repositories;

import com.example.club.entities.PagoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoCuotaRepository extends JpaRepository<PagoCuota, Long> {
    List<PagoCuota> findBySocio_IdOrderByFechaPagoDesc(Long idSocio);
    List<PagoCuota> findByFamilia_IdOrderByFechaPagoDesc(Long idFamilia);
}
